# build-and-push.ps1

# Set your Docker Hub username
$DOCKER_USERNAME = "felipec03"

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
    },
    @{
        Name = "ms-descuentoporpersona"
        Path = "./ms-descuentoporpersona"
        Tag = "latest"
    },
    @{
        Name = "ms-descuentosclientefrecuente"
        Path = "./ms-descuentosclientefrecuente"
        Tag = "latest"
    },
    @{
        Name = "ms-tarifadiaespecial"
        Path = "./ms-tarifadiaespecial"
        Tag = "latest"
    },
    @{
        Name = "ms-registroreserva-comprobantepago"
        Path = "./ms-registroreserva-comprobantepago"
        Tag = "latest"
    },
    @{
        Name = "ms-racksemanal"
        Path = "./ms-racksemanal"
        Tag = "latest"
    },
    @{
        Name = "ms-reportes-vueltas-personas"
        Path = "./ms-reportes-vueltas-personas"
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

# Assuming already logged in to Docker Hub
Write-Status "Using existing Docker Hub login for $DOCKER_USERNAME" "INFO"

# Build and push each microservice
foreach ($service in $MICROSERVICES) {
    $imageName = "$($DOCKER_USERNAME)/$($service.Name):$($service.Tag)"
    
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
    
    # Push the Docker image to Docker Hub
    Write-Status "Pushing $($service.Name) to Docker Hub..."
    try {
        docker push $imageName
        Write-Status "Successfully pushed $($service.Name) to Docker Hub" "SUCCESS"
    }
    catch {
        Write-Status "Failed to push $($service.Name): $_" "ERROR"
    }
}

Write-Status "All operations completed" "SUCCESS"
Write-Status "Next steps: Update your Kubernetes deployment files to use Docker Hub images" "INFO"
Write-Status "Image format will be: $DOCKER_USERNAME/service-name:tag" "INFO"