# ShopVerse Centralized Configuration Guide

## Table of Contents
1. [Overview](#overview)
2. [Repository Structure](#repository-structure)
3. [Setup Instructions](#setup-instructions)
4. [Adding a New Service](#adding-a-new-service)
5. [Updating Configuration](#updating-configuration)
6. [Best Practices](#best-practices)
7. [Troubleshooting](#troubleshooting)

## Overview
This documentation explains how to manage the centralized configuration for ShopVerse microservices using Spring Cloud Config Server and GitHub.

## Repository Structure
```
config-repo/
├── application.yml           # Common configurations for all services
├── discovery-service/        # Discovery service configurations
│   └── discovery-service.yml
└── user-service/             # User service configurations
    └── user-service.yml
```

## Setup Instructions

### Prerequisites
- Java 21+
- Git
- MySQL
- GitHub account with repository access

### 1. Clone the Configuration Repository
```bash
git clone https://github.com/iKirtesh/shopverse-config-repo.git
cd shopverse-config-repo
```

### 2. Set Up Environment Variables
```bash
# On Windows
set GITHUB_USERNAME=your-github-username
set GITHUB_TOKEN=your-github-token

# On Linux/Mac
export GITHUB_USERNAME=your-github-username
export GITHUB_TOKEN=your-github-token
```

### 3. Start the Services
Start services in this order:
1. Config Server
2. Discovery Service (Eureka)
3. Other microservices

## Adding a New Service

### 1. Create Service Configuration
1. Create a new directory for your service in the config-repo:
   ```bash
   mkdir config-repo/your-service-name
   ```

2. Create a configuration file (e.g., `your-service-name.yml`):
   ```yaml
   server:
     port: 8082  # Choose an available port

   spring:
     application:
       name: your-service-name
     # Add service-specific configurations here
   ```

### 2. Update Service to Use Config Server
Add to your service's [bootstrap.yml](cci:7://file:///C:/CDAC/Project/ShopVerse-Backend/user/src/main/resources/bootstrap.yml:0:0-0:0):
```yaml
spring:
  application:
    name: your-service-name
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
      retry:
        initial-interval: 1000
        max-interval: 2000
        max-attempts: 6
        multiplier: 1.1
```

### 3. Add Required Dependencies
Add these to your service's [build.gradle](cci:7://file:///C:/CDAC/Project/ShopVerse-Backend/user/build.gradle:0:0-0:0):
```gradle
dependencies {
    // Spring Cloud Config Client
    implementation 'org.springframework.cloud:spring-cloud-starter-config'
    implementation 'org.springframework.cloud:spring-cloud-starter-bootstrap'
    
    // Eureka Client (if service needs to register with Eureka)
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
    
    // Actuator (for health checks and metrics)
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

## Updating Configuration

### 1. Update Configuration
1. Edit the appropriate YAML file in the config-repo
2. Commit and push changes:
   ```bash
   git add .
   git commit -m "Update configuration for service-name"
   git push origin main
   ```

### 2. Refresh Configuration at Runtime
To refresh configuration without restarting the service:
```bash
# Send POST request to refresh endpoint
curl -X POST http://localhost:<service-port>/actuator/refresh
```

## Best Practices

### Security
- Never commit sensitive data to the repository
- Use environment variables for secrets
- Rotate GitHub tokens regularly
- Restrict repository access

### Configuration Management
- Keep common configurations in [application.yml](cci:7://file:///C:/CDAC/Project/ShopVerse-Backend/config-repo/application.yml:0:0-0:0)
- Use profiles for environment-specific configurations
- Document all configuration properties

### Version Control
- Use meaningful commit messages
- Create branches for major changes
- Use pull requests for code review

## Troubleshooting

### Common Issues

#### Config Server Cannot Connect to GitHub
1. Verify GitHub token has correct permissions
2. Check network connectivity
3. Verify repository URL and branch name

#### Service Cannot Connect to Config Server
1. Verify Config Server is running
2. Check service name matches configuration file name
3. Verify bootstrap.yml is in the correct location

#### Configuration Changes Not Reflected
1. Check if the change was pushed to the correct branch
2. Verify the service is pointing to the right Config Server
3. Check service logs for configuration loading errors

### Logs to Check
- Config Server logs for GitHub connectivity
- Service startup logs for configuration loading
- Eureka dashboard for service registration

## Monitoring
Access these endpoints for monitoring:

| Service | URL | Description |
|---------|-----|-------------|
| Config Server | http://localhost:8888/actuator/health | Config Server health |
| Eureka Dashboard | http://localhost:8761 | Service registry |
| Service Config | http://localhost:8888/{service-name}/{profile} | View service configuration |

## Contact
For support, contact the DevOps team or create an issue in the GitHub repository.