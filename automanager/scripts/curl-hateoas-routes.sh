#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

command -v curl >/dev/null 2>&1 || { echo "curl is required" >&2; exit 1; }
command -v jq >/dev/null 2>&1 || { echo "jq is required" >&2; exit 1; }

tmp_dir="$(mktemp -d)"
trap 'rm -rf "$tmp_dir"' EXIT

request_get() {
	local path="$1"
	local response_file="$tmp_dir/response.json"
	local status_file="$tmp_dir/status.txt"

	curl -sS -o "$response_file" -w '%{http_code}' "$BASE_URL$path" > "$status_file"

	echo "$response_file"
	echo "$status_file"
}

print_collection() {
	local path="$1"
	local label="$2"
	local response_file status_file status
	local files
	mapfile -t files < <(request_get "$path")
	response_file="${files[0]}"
	status_file="${files[1]}"
	status="$(cat "$status_file")"

	echo "=== $label [$path] status=$status ==="
	jq '.' "$response_file"
	echo "links:"
	jq -r '._links | keys[]' "$response_file" | sed 's/^/- /'
	echo "item self links:"
	jq -r '._embedded? // empty | .. | objects | select(._links? and ._links.self?) | ._links.self.href' "$response_file" \
		| sed '/^$/d' | sed 's/^/- /'
	echo
}

print_resource() {
	local path="$1"
	local label="$2"
	local response_file status_file status
	local files
	mapfile -t files < <(request_get "$path")
	response_file="${files[0]}"
	status_file="${files[1]}"
	status="$(cat "$status_file")"

	echo "=== $label [$path] status=$status ==="
	jq '.' "$response_file"
	echo "links:"
	jq -r '._links | to_entries[] | "- \(.key): \(.value.href)"' "$response_file"
	echo
}

first_id_from_collection() {
	local path="$1"
	curl -sS "$BASE_URL$path" \
		| jq -r '._embedded? // empty | .. | objects | select(.id? and ._links? and ._links.self?) | .id' \
		| head -n 1
}

first_client_id() {
	curl -sS "$BASE_URL/usuario" \
		| jq -r '._embedded? // empty | .. | objects | select(.perfis? and (.perfis | index("CLIENTE")) != null) | .id' \
		| head -n 1
}

echo "Base URL: $BASE_URL"
echo

print_collection /empresa Empresa
print_collection /mercadoria Mercadoria
print_collection /servico Servico
print_collection /usuario Usuario
print_collection /venda Venda

empresa_id="$(first_id_from_collection /empresa)"
usuario_id="$(first_id_from_collection /usuario)"
cliente_id="$(first_client_id)"
venda_id="$(first_id_from_collection /venda)"
veiculo_id="$(first_id_from_collection /veiculo)"

if [[ -n "$empresa_id" && "$empresa_id" != "null" ]]; then
	print_resource "/empresa/$empresa_id" "Empresa detail"
fi

if [[ -n "$usuario_id" && "$usuario_id" != "null" ]]; then
	print_resource "/usuario/$usuario_id" "Usuario detail"
	print_collection "/usuario/$usuario_id/credencial" "Credenciais do usuario"
fi

if [[ -n "$cliente_id" && "$cliente_id" != "null" ]]; then
	print_collection "/usuario/$cliente_id/veiculo" "Veiculos do cliente"
fi

if [[ -n "$venda_id" && "$venda_id" != "null" ]]; then
	print_resource "/venda/$venda_id" "Venda detail"
fi

if [[ -n "$empresa_id" && "$empresa_id" != "null" && -n "$usuario_id" && "$usuario_id" != "null" ]]; then
	print_collection "/empresa/$empresa_id/usuario" "Usuarios by empresa"
	print_resource "/usuario/$usuario_id/empresa/$empresa_id" "Usuario associado a empresa"
fi

if [[ -n "$cliente_id" && "$cliente_id" != "null" ]]; then
	print_collection "/venda/usuario/$cliente_id" "Vendas por cliente"
fi

if [[ -n "$veiculo_id" && "$veiculo_id" != "null" ]]; then
	print_resource "/veiculo/$veiculo_id" "Veiculo detail"
fi
