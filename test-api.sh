#!/bin/bash

# Set base URL
BASE_URL="http://localhost:8081"

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# Function to check if jq is installed
check_jq() {
  if ! command -v jq &> /dev/null; then
    print_warning "jq is not installed. JSON responses will not be pretty-printed."
    print_warning "You can install it with: brew install jq"
  fi
}

# Function to pretty print JSON if jq is available
pretty_print_json() {
  if command -v jq &> /dev/null; then
    echo "$1" | jq . 2>/dev/null || echo "$1"
  else
    echo "$1"
  fi
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
  
  pretty_print_json "$response"
  
  if echo "$response" | grep -q "User registered successfully"; then
    print_success "User registered successfully!"
    return 0
  else
    print_error "User registration failed!"
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
  
  # Display the raw response for debugging
  echo "Raw login response:"
  echo "$response"
  echo
  
  # Try to pretty print if possible
  print_message "Formatted response:"
  pretty_print_json "$response"
  
  # Extract token if login successful
  if [[ "$response" == *"token"* ]]; then
    # More robust token extraction
    local token=$(echo "$response" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
    print_success "Login successful! JWT token received."
    echo "$token"
    return 0
  else
    print_error "Login failed!"
    return 1
  fi
}

# Function to get all users (needs authentication)
get_users() {
  local token=$1
  
  print_message "Getting user list with JWT token"
  print_message "Token length: ${#token}"
  print_message "Token: $token"
  
  local response=$(curl -s -v "$BASE_URL/api/test/users" \
    -H "Authorization: Bearer $token" \
    -H "Accept: application/json" 2>&1)
  
  pretty_print_json "$response"
  
  if [[ "$response" == *"id"* ]]; then
    print_success "Get users successful!"
    return 0
  else
    print_error "Get users failed!"
    return 1
  fi
}

# Main function
main() {
  check_jq
  
  echo "============================================="
  echo "  Pharmacy Inventory API Testing Script"
  echo "============================================="
  echo
  
  # Generate unique email for testing
  TEST_EMAIL=$(generate_email)
  TEST_PASSWORD="password123"
  TEST_NAME="Test User $(date +%s)"
  
  # Test case 1: Register a new user
  echo "---------------------------------------------"
  echo "TEST 1: Register a new user"
  echo "---------------------------------------------"
  register_user "$TEST_NAME" "$TEST_EMAIL" "$TEST_PASSWORD" "[\"admin\"]"
  echo
  
  # Test case 2: Login with the registered user
  echo "---------------------------------------------"
  echo "TEST 2: Login with registered user"
  echo "---------------------------------------------"
  TOKEN=$(login "$TEST_EMAIL" "$TEST_PASSWORD")
  echo "Main token value: $TOKEN"
  echo
  
  if [[ -n "$TOKEN" ]]; then
    # Test case 3: Access protected resource
    echo "---------------------------------------------"
    echo "TEST 3: Access protected endpoint (users list)"
    echo "---------------------------------------------"
    get_users "$TOKEN"
    echo
  else
    print_error "Skipping authentication test as login failed."
  fi
  
  echo "============================================="
  echo "  Testing Complete"
  echo "============================================="
}

# Run the main function
main
