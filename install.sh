#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}==================================="
echo "Agentic Workspace Installation"
echo -e "===================================${NC}"
echo ""

# Check if .env file exists
if [ -f ".env" ]; then
    echo -e "${GREEN}✓${NC} .env file found"
    set -a
    source .env
    set +a
else
    echo -e "${RED}ERROR: .env file not found!${NC}"
    echo "Please copy .env.example to .env and configure your API keys:"
    echo "  cp .env.example .env"
    echo "  # Edit .env with your API keys"
    exit 1
fi

# Check if Claude Code is installed
claude --version > /dev/null 2>&1 || { 
    echo -e "${RED}ERROR: claude CLI not found!${NC}"
    echo "Please install the claude CLI and ensure it's in your PATH."
    echo exit 1
}

# Add the MCP to Claude Code
claude mcp list | grep "sonarqube-mcp-with-cag" > /dev/null 2>&1    || {
    echo -e "${YELLOW}MCP 'sonarqube-mcp-with-cag' not found. Adding it now...${NC}"

    claude mcp add sonarqube-mcp-with-cag \
    --env SONARQUBE_TOOLSETS="cag,projects,analysis" \
    --env SONARQUBE_ADVANCED_ANALYSIS_ENABLED="true" \
    -- docker run -i --rm -e SONARQUBE_TOKEN \
    -e SONARQUBE_URL -e SONARQUBE_ORG -e SONAR_SQ_PROJECT_KEY \
    -e SONAR_SQ_BRANCH -e SONARQUBE_TOOLSETS -e SONARQUBE_ADVANCED_ANALYSIS_ENABLED \
    -v "$(pwd):/app/mcp-workspace:rw" \
    sq-mcp-cag-alpha:20260303-8a85a46-arm64
}
