#!/bin/bash

# Set base URL
BASE_URL="http://localhost:8081"

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Token storage
TOKEN=""
EMAIL=""
PASSWORD="password123"

# Function to print colored messages
print_message() {
  echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
  echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
  echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
  echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_endpoint() {
  echo -e "${YELLOW}====== Testing:${NC} $1 ${YELLOW}======${NC}"
}

# Generate a unique email for testing
generate_email() {
  local timestamp=$(date +%s)
  echo "test-$timestamp@pharmacy.com"
}

# Function to register a user
register_user() {
  local name=$1
  local email=$2
  local password=$3
  local roles=$4
  
  print_message "Registering user with email: $email"
  
  local response=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d "{\"name\":\"$name\", \"email\":\"$email\", \"password\":\"$password\", \"roles\":$roles}")
  
  if echo "$response" | grep -q "User registered successfully"; then
    print_success "User registered successfully!"
    return 0
  else
    print_error "User registration failed! Response: $response"
    return 1
  fi
}

# Function to login
login() {
  local email=$1
  local password=$2
  
  print_message "Logging in with email: $email"
  
  local response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d "{\"email\":\"$email\", \"password\":\"$password\"}")
  
  if [[ "$response" == *"token"* ]]; then
    TOKEN=$(echo "$response" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
    print_success "Login successful! JWT token received."
    return 0
  else
    print_error "Login failed! Response: $response"
    return 1
  fi
}

# Extract ID from JSON response
extract_id() {
  local content=$1
  
  # Try to extract UUID format (with quotes)
  local uuid=$(echo "$content" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
  
  # If found, return it
  if [[ -n "$uuid" ]]; then
    echo "$uuid"
    return 0
  fi
  
  # Try to extract numeric ID
  local numeric_id=$(echo "$content" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
  
  # Return numeric ID or empty string
  echo "$numeric_id"
}

# Function to make authenticated GET request
auth_get() {
  local endpoint=$1
  local description=$2
  
  print_endpoint "GET $endpoint - $description"
  
  local response=$(curl -s -w "\n%{http_code}" "$BASE_URL$endpoint" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Accept: application/json")
  
  local http_code=$(echo "$response" | tail -n1)
  local content=$(echo "$response" | sed '$d')
  
  if [[ "$http_code" -ge 200 && "$http_code" -lt 300 ]]; then
    print_success "GET $endpoint successful (HTTP $http_code)"
    echo "$content" | head -n 50  # Show first 50 lines to avoid excessive output
    return 0
  else
    print_error "GET $endpoint failed (HTTP $http_code)"
    echo "$content"
    return 1
  fi
}

# Function to make authenticated POST request
auth_post() {
  local endpoint=$1
  local data=$2
  local description=$3
  local temp_file=$(mktemp)
  
  print_endpoint "POST $endpoint - $description"
  
  local response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL$endpoint" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d "$data")
  
  local http_code=$(echo "$response" | tail -n1)
  local content=$(echo "$response" | sed '$d')
  
  # Save to temp file for extraction
  echo "$content" > "$temp_file"
  
  if [[ "$http_code" -ge 200 && "$http_code" -lt 300 ]]; then
    print_success "POST $endpoint successful (HTTP $http_code)"
    echo "$content" | head -n 50
    
    # Extract ID from the response
    local id=$(extract_id "$content")
    echo "$id" > /tmp/last_id.txt
    return 0
  else
    print_error "POST $endpoint failed (HTTP $http_code)"
    echo "$content"
    echo "" > /tmp/last_id.txt
    return 1
  fi
}

# Function to make authenticated PUT request
auth_put() {
  local endpoint=$1
  local data=$2
  local description=$3
  
  print_endpoint "PUT $endpoint - $description"
  
  local response=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL$endpoint" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d "$data")
  
  local http_code=$(echo "$response" | tail -n1)
  local content=$(echo "$response" | sed '$d')
  
  if [[ "$http_code" -ge 200 && "$http_code" -lt 300 ]]; then
    print_success "PUT $endpoint successful (HTTP $http_code)"
    echo "$content" | head -n 50
    return 0
  else
    print_error "PUT $endpoint failed (HTTP $http_code)"
    echo "$content"
    return 1
  fi
}

# Function to make authenticated DELETE request
auth_delete() {
  local endpoint=$1
  local description=$2
  
  print_endpoint "DELETE $endpoint - $description"
  
  local response=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL$endpoint" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Accept: application/json")
  
  local http_code=$(echo "$response" | tail -n1)
  local content=$(echo "$response" | sed '$d')
  
  if [[ "$http_code" -ge 200 && "$http_code" -lt 300 ]]; then
    print_success "DELETE $endpoint successful (HTTP $http_code)"
    echo "$content" | head -n 50
    return 0
  else
    print_error "DELETE $endpoint failed (HTTP $http_code)"
    echo "$content"
    return 1
  fi
}

# Function to get the last created ID
get_last_id() {
  if [[ -f "/tmp/last_id.txt" ]]; then
    cat "/tmp/last_id.txt"
  else
    echo ""
  fi
}

# Main testing function
run_tests() {
  print_message "Testing Auth Controller Endpoints..."
  # Auth endpoints tested during setup
  
  print_message "Testing Branch Controller Endpoints..."
  # Test Branch endpoints
  auth_get "/api/branches" "Get all branches"
  local branch_data='{"name":"Test Branch","address":"123 Test St","phone":"1234567890","email":"branch@test.com"}'
  
  # Create branch
  auth_post "/api/branches" "$branch_data" "Create a new branch"
  local branch_id=$(get_last_id)
  
  if [[ -n "$branch_id" ]]; then
    print_message "Created branch with ID: $branch_id"
    auth_get "/api/branches/$branch_id" "Get branch by ID"
    auth_put "/api/branches/$branch_id" "${branch_data/Test Branch/Updated Branch}" "Update branch"
    auth_delete "/api/branches/$branch_id" "Delete branch"
  fi
  
  print_message "Testing Medicine Controller Endpoints..."
  # Test Medicine endpoints
  auth_get "/api/medicines" "Get all medicines"
  local medicine_data='{"name":"Test Medicine","description":"Test Description","manufacturer":"Test Mfg","category":"Test Category","unitPrice":10.99,"stock":100}'
  
  # Create medicine
  auth_post "/api/medicines" "$medicine_data" "Create a new medicine"
  local medicine_id=$(get_last_id)
  
  if [[ -n "$medicine_id" ]]; then
    print_message "Created medicine with ID: $medicine_id"
    auth_get "/api/medicines/$medicine_id" "Get medicine by ID"
    auth_put "/api/medicines/$medicine_id" "${medicine_data/Test Medicine/Updated Medicine}" "Update medicine"
    # Don't delete medicine yet as other tests might need it
  fi
  
  print_message "Testing Barcode Controller Endpoints..."
  # Test Barcode endpoints
  if [[ -n "$medicine_id" ]]; then
    local barcode_data="{\"medicineId\":\"$medicine_id\"}"
    
    # Generate barcode
    auth_post "/api/barcodes" "$barcode_data" "Generate new barcode"
    local barcode=$(get_last_id)
    
    if [[ -n "$barcode" ]]; then
      print_message "Generated barcode: $barcode"
      auth_get "/api/barcodes/$barcode" "Get medicine by barcode"
    fi
    
    # Test barcode POST endpoints
    echo -e "\n🔍 Testing Barcode Controller POST endpoints..."
    barcode_post=$(curl -s -X POST \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/x-www-form-urlencoded" \
      -d "content=TEST123&width=300&height=100" \
      "$BASE_URL/api/barcodes/create")
    echo "POST /api/barcodes/create: $barcode_post"

    qrcode_post=$(curl -s -X POST \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/x-www-form-urlencoded" \
      -d "content=https://example.com&width=250&height=250" \
      "$BASE_URL/api/barcodes/qr/create")
    echo "POST /api/barcodes/qr/create: $qrcode_post"
  fi
  
  print_message "Testing Batch Controller Endpoints..."
  # Test Batch endpoints
  auth_get "/api/batches" "Get all batches"
  if [[ -n "$medicine_id" ]]; then
    local batch_data="{\"medicineId\":\"$medicine_id\",\"quantity\":50,\"manufacturingDate\":\"2025-01-01\",\"expiryDate\":\"2026-01-01\",\"batchNumber\":\"BATCH-001\"}"
    
    # Create batch
    auth_post "/api/batches" "$batch_data" "Create a new batch"
    local batch_id=$(get_last_id)
    
    if [[ -n "$batch_id" ]]; then
      print_message "Created batch with ID: $batch_id"
      auth_get "/api/batches/$batch_id" "Get batch by ID"
      auth_put "/api/batches/$batch_id" "${batch_data/BATCH-001/BATCH-002}" "Update batch"
      auth_delete "/api/batches/$batch_id" "Delete batch"
    fi
  fi
  
  print_message "Testing Notification Controller Endpoints..."
  # Test Notification endpoints
  auth_get "/api/notifications" "Get all notifications"
  auth_get "/api/notifications/1" "Get notification by ID"
  auth_post "/api/notifications/mark-read/1" "{}" "Mark notification as read"
  
  echo -e "\n🔍 Testing Notification Controller POST endpoint..."
  # Get a user ID for creating notification
  user_id=$(get_last_id "user")
  notification_create=$(curl -s -X POST \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"message\": \"Test notification message\",
      \"type\": \"LOW_STOCK\",
      \"userId\": \"$user_id\"
    }" \
    "$BASE_URL/api/notifications")
  echo "POST /api/notifications: $notification_create"
  
  print_message "Testing Prescription Controller Endpoints..."
  # Test Prescription endpoints
  auth_get "/api/prescriptions" "Get all prescriptions"
  local prescription_data="{\"patientName\":\"Test Patient\",\"doctorName\":\"Test Doctor\",\"issueDate\":\"2025-03-30\",\"notes\":\"Test prescription notes\"}"
  
  # Create prescription
  auth_post "/api/prescriptions" "$prescription_data" "Create a new prescription"
  local prescription_id=$(get_last_id)
  
  if [[ -n "$prescription_id" ]]; then
    print_message "Created prescription with ID: $prescription_id"
    auth_get "/api/prescriptions/$prescription_id" "Get prescription by ID"
    auth_put "/api/prescriptions/$prescription_id" "${prescription_data/Test Patient/Updated Patient}" "Update prescription"
    auth_delete "/api/prescriptions/$prescription_id" "Delete prescription"
  fi
  
  print_message "Testing Purchase Order Controller Endpoints..."
  # Test Purchase Order endpoints
  auth_get "/api/purchase-orders" "Get all purchase orders"
  local purchase_order_data="{\"supplierId\":\"1\",\"orderDate\":\"2025-03-30\",\"expectedDeliveryDate\":\"2025-04-05\",\"status\":\"PENDING\",\"totalAmount\":1099.99}"
  
  # Create purchase order
  auth_post "/api/purchase-orders" "$purchase_order_data" "Create a new purchase order"
  local purchase_order_id=$(get_last_id)
  
  if [[ -n "$purchase_order_id" ]]; then
    print_message "Created purchase order with ID: $purchase_order_id"
    auth_get "/api/purchase-orders/$purchase_order_id" "Get purchase order by ID"
    auth_put "/api/purchase-orders/$purchase_order_id" "${purchase_order_data/PENDING/DELIVERED}" "Update purchase order"
    auth_delete "/api/purchase-orders/$purchase_order_id" "Delete purchase order"
  fi
  
  print_message "Testing Report Controller Endpoints..."
  # Test Report endpoints
  auth_get "/api/reports/sales" "Generate sales report"
  auth_get "/api/reports/inventory" "Generate inventory report"
  auth_get "/api/reports/expiry" "Generate expiry report"
  
  print_message "Testing Sale Controller Endpoints..."
  # Test Sale endpoints
  auth_get "/api/sales" "Get all sales"
  local sale_data="{\"customerId\":\"1\",\"saleDate\":\"2025-03-30\",\"totalAmount\":99.99,\"paymentMethod\":\"CASH\",\"status\":\"COMPLETED\"}"
  
  # Create sale
  auth_post "/api/sales" "$sale_data" "Create a new sale"
  local sale_id=$(get_last_id)
  
  if [[ -n "$sale_id" ]]; then
    print_message "Created sale with ID: $sale_id"
    auth_get "/api/sales/$sale_id" "Get sale by ID"
    auth_put "/api/sales/$sale_id" "${sale_data/CASH/CARD}" "Update sale"
    auth_delete "/api/sales/$sale_id" "Delete sale"
  fi
  
  echo -e "\n🔍 Testing Sale Controller POST endpoint..."
  # Get necessary IDs for creating a sale
  user_id=$(get_last_id "user")
  branch_id=$(get_last_id "branch")
  medicine_id=$(get_last_id "medicine")

  sale_create=$(curl -s -X POST \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"userId\": \"$user_id\",
      \"totalAmount\": 100.50,
      \"customerName\": \"Test Customer\",
      \"branchId\": \"$branch_id\",
      \"saleItems\": [
        {
          \"medicineId\": \"$medicine_id\",
          \"quantity\": 2,
          \"totalPrice\": 100.50
        }
      ]
    }" \
    "$BASE_URL/api/sales")
  echo "POST /api/sales: $sale_create"
  
  print_message "Testing Stock Controller Endpoints..."
  # Test Stock endpoints
  auth_get "/api/stock" "Get all stock"
  
  if [[ -n "$branch_id" ]]; then
    auth_get "/api/stock/branch/$branch_id" "Get stock by branch"
  fi
  
  if [[ -n "$medicine_id" ]]; then
    auth_get "/api/stock/medicine/$medicine_id" "Get stock by medicine"
    
    if [[ -n "$branch_id" ]]; then
      local transfer_data="{\"fromBranchId\":\"1\",\"toBranchId\":\"$branch_id\",\"medicineId\":\"$medicine_id\",\"quantity\":10}"
      auth_post "/api/stock/transfer" "$transfer_data" "Transfer stock between branches"
    fi
    
    # Test stock transfer endpoint
    echo -e "\n🔍 Testing Stock Transfer endpoint..."
    # Get branch IDs for transfer
    branch_id_1=$(get_last_id "branch")
    branch_id_2=$(curl -s -X POST \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "name": "Branch for Transfer",
        "address": "123 Transfer St",
        "phoneNumber": "9998887776",
        "email": "transfer@example.com"
      }' \
      "$BASE_URL/api/branches" | extract_id)
    echo "Created second branch with ID: $branch_id_2"

    # Get a medicine ID
    medicine_id=$(get_last_id "medicine")

    # Test stock by branch ID endpoint
    echo -e "\n🔍 Testing Get Stock by Branch ID..."
    stock_by_branch=$(curl -s -X GET \
      -H "Authorization: Bearer $TOKEN" \
      "$BASE_URL/api/stock/branch/$branch_id_1")
    echo "GET /api/stock/branch/$branch_id_1: $stock_by_branch"

    # Test stock transfer
    stock_transfer=$(curl -s -X POST \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d "{
        \"fromBranchId\": \"$branch_id_1\",
        \"toBranchId\": \"$branch_id_2\",
        \"medicineId\": \"$medicine_id\",
        \"quantity\": 5
      }" \
      "$BASE_URL/api/stock/transfer")
    echo "POST /api/stock/transfer: $stock_transfer"
  fi
  
  print_message "Testing Stock Transfer Controller Endpoints..."
  # Get all stock transfers
  echo -e "\n🔍 Testing Get All Stock Transfers..."
  stock_transfers=$(curl -s -X GET \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL/api/stock-transfers")
  echo "GET /api/stock-transfers: $stock_transfers"

  # Create a stock transfer
  echo -e "\n🔍 Testing Create Stock Transfer..."
  stock_transfer_create=$(curl -s -X POST \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "fromBranchId": "'$branch_id_1'",
      "toBranchId": "'$branch_id_2'",
      "medicineId": "'$medicine_id'",
      "quantity": 5
    }' \
    "$BASE_URL/api/stock-transfers")
  echo "POST /api/stock-transfers: $stock_transfer_create"

  # Get stock transfer by ID (extract ID from creation response if available)
  if [[ $stock_transfer_create == *"id"* ]]; then
    TRANSFER_ID=$(echo $stock_transfer_create | grep -o '"id":"[^"]*' | sed 's/"id":"//')
    echo -e "\n🔍 Testing Get Stock Transfer by ID..."
    stock_transfer=$(curl -s -X GET \
      -H "Authorization: Bearer $TOKEN" \
      "$BASE_URL/api/stock-transfers/$TRANSFER_ID")
    echo "GET /api/stock-transfers/$TRANSFER_ID: $stock_transfer"
    
    # Process stock transfer
    echo -e "\n🔍 Testing Process Stock Transfer..."
    stock_transfer_process=$(curl -s -X PATCH \
      -H "Authorization: Bearer $TOKEN" \
      "$BASE_URL/api/stock-transfers/$TRANSFER_ID/process?status=COMPLETED")
    echo "PATCH /api/stock-transfers/$TRANSFER_ID/process: $stock_transfer_process"
  fi

  # Get stock transfers by source branch
  echo -e "\n🔍 Testing Get Stock Transfers by Source Branch..."
  source_transfers=$(curl -s -X GET \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL/api/stock-transfers/from-branch/$branch_id_1")
  echo "GET /api/stock-transfers/from-branch/$branch_id_1: $source_transfers"

  # Get stock transfers by destination branch
  echo -e "\n🔍 Testing Get Stock Transfers by Destination Branch..."
  dest_transfers=$(curl -s -X GET \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL/api/stock-transfers/to-branch/$branch_id_2")
  echo "GET /api/stock-transfers/to-branch/$branch_id_2: $dest_transfers"

  print_message "Testing Supplier Controller Endpoints..."
  # Test Supplier endpoints
  auth_get "/api/suppliers" "Get all suppliers"
  local supplier_data="{\"name\":\"Test Supplier\",\"address\":\"456 Supplier St\",\"phone\":\"9876543210\",\"email\":\"supplier@test.com\"}"
  
  # Create supplier
  auth_post "/api/suppliers" "$supplier_data" "Add a new supplier"
  local supplier_id=$(get_last_id)
  
  if [[ -n "$supplier_id" ]]; then
    print_message "Created supplier with ID: $supplier_id"
    auth_get "/api/suppliers/$supplier_id" "Get supplier by ID"
    auth_put "/api/suppliers/$supplier_id" "${supplier_data/Test Supplier/Updated Supplier}" "Update supplier"
    auth_delete "/api/suppliers/$supplier_id" "Delete supplier"
  fi
  
  # Clean up created resources
  if [[ -n "$medicine_id" ]]; then
    auth_delete "/api/medicines/$medicine_id" "Delete test medicine"
  fi
}

# Main function
main() {
  echo "============================================="
  echo "  Pharmacy Inventory API Endpoint Tester"
  echo "============================================="
  echo
  
  # Generate unique email for testing
  EMAIL=$(generate_email)
  
  # Register admin user
  register_user "Test Admin" "$EMAIL" "$PASSWORD" "[\"admin\"]"
  
  # Login to get JWT token
  login "$EMAIL" "$PASSWORD"
  
  if [[ -n "$TOKEN" ]]; then
    # Run all endpoint tests
    run_tests
  else
    print_error "Authentication failed. Cannot proceed with API testing."
    exit 1
  fi
  
  echo "============================================="
  echo "  API Testing Complete"
  echo "============================================="
}

# Run the main function
main
