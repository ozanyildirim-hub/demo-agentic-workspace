# demo-agentic-workspace
This is a workspace to demonstrate Sonar integrated with agentic coding workflows.

There is a simple Java application set up to use as a basis for context augmentation.

## Requirements
- Java 17
- Docker
- npm
- Claude Code

## Instructions

1. Download & Load the ALPHA Docker Image from GitHub ( https://github.com/SonarSource/sonar-caas-poc/actions/runs/22636379448/artifacts/5746183653 )

2. Extract: Unzip to get the .tar file

3. Load the image:
docker load -i sq-mcp-cag-alpha-YYYYMMDD-{sha}-{arch}.tar

4. copy .env.example to .env and set the environment variables

5. Run `install.sh` to set up the environment (do this every time)

6. Vibe then Verify!
