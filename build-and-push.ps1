# build-and-push.ps1

# Set your Azure Container Registry name
$ACR_NAME = "clustertingesoregistry"
$RESOURCE_GROUP = "tingesoEntrega2"

# Array of microservices to build and push
$MICROSERVICES = @(
    @{
        Name = "config-server"
        Path = "./config-server"
        Tag = "latest"
    },
    @{
        Name = "eureka-server" 
        Path = "./eureka-server"
        Tag = "latest"
    },
    @{
        Name = "gateway-server"
        Path = "./gateway-server" 
        Tag = "latest"
    },
    @{
        Name = "ms-tarifasconfig"
        Path = "./ms-tarifasconfig"
        Tag = "latest"
    }
)

# Function to show status messages
function Write-Status {
    param (
        [string]$Message,
        [string]$Status = "INFO"
    )
    
    $color = switch ($Status) {
        "INFO" { "Cyan" }
        "SUCCESS" { "Green" }
        "ERROR" { "Red" }
        "WARNING" { "Yellow" }
        default { "White" }
    }
    
    Write-Host "[$Status] $Message" -ForegroundColor $color
}

# Login to Azure
Write-Status "Logging in to Azure..."
try {
    az login
    Write-Status "Azure login successful" "SUCCESS"
}
catch {
    Write-Status "Failed to login to Azure: $_" "ERROR"
    exit 1
}

# Login to ACR
Write-Status "Logging in to Azure Container Registry ($ACR_NAME)..."
try {
    az acr login --name $ACR_NAME
    Write-Status "ACR login successful" "SUCCESS"
}
catch {
    Write-Status "Failed to login to ACR: $_" "ERROR"
    exit 1
}

# Build and push each microservice
foreach ($service in $MICROSERVICES) {
    $imageName = "$($ACR_NAME).azurecr.io/$($service.Name):$($service.Tag)"
    
    # Check if directory exists
    if (-not (Test-Path $service.Path)) {
        Write-Status "Directory $($service.Path) not found. Skipping $($service.Name)." "WARNING"
        continue
    }
    
    # Build the Docker image
    Write-Status "Building Docker image for $($service.Name)..."
    try {
        docker build -t $imageName $service.Path
        Write-Status "Successfully built $($service.Name)" "SUCCESS"
    }
    catch {
        Write-Status "Failed to build $($service.Name): $_" "ERROR"
        continue
    }
    
    # Push the Docker image to ACR
    Write-Status "Pushing $($service.Name) to ACR..."
    try {
        docker push $imageName
        Write-Status "Successfully pushed $($service.Name) to ACR" "SUCCESS"
    }
    catch {
        Write-Status "Failed to push $($service.Name): $_" "ERROR"
    }
}

Write-Status "All operations completed" "SUCCESS"
Write-Status "Next steps: Update your Kubernetes deployment files and deploy your services" "INFO"