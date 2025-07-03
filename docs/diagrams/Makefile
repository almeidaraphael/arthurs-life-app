# Makefile for generating PlantUML diagrams

# Default target
.PHONY: all
all: svg

# Generate PNG images (local PlantUML)
.PHONY: png
png:
	@echo "Generating PNG diagrams..."
	@plantuml -tpng diagrams/*.puml
	@echo "PNG diagrams generated successfully!"

# Generate SVG images (local PlantUML) - recommended for documentation
.PHONY: svg
svg:
	@echo "Generating SVG diagrams..."
	@plantuml -tsvg *.puml
	@echo "SVG diagrams generated successfully!"

# Generate PDF images (local PlantUML)
.PHONY: pdf
pdf:
	@echo "Generating PDF diagrams..."
	@plantuml -tpdf diagrams/*.puml
	@echo "PDF diagrams generated successfully!"

# Generate PNG images using Docker
.PHONY: docker-png
docker-png:
	@echo "Generating PNG diagrams with Docker..."
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tpng /data/c4-context.puml
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tpng /data/c4-container.puml
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tpng /data/c4-component.puml	
	@echo "PNG diagrams generated successfully with Docker!"

# Generate SVG images using Docker (recommended)
.PHONY: docker-svg
docker-svg:
	@echo "Generating SVG diagrams with Docker..."
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/c4-context.puml
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/c4-container.puml
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/c4-component.puml
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/sequence-task-completion.puml
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/accessibility-architecture.puml
	@echo "SVG diagrams generated successfully with Docker!"

# Generate PDF images using Docker
.PHONY: docker-pdf
docker-pdf:
	@echo "Generating PDF diagrams with Docker..."
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tpdf /data/c4-context.puml
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tpdf /data/c4-container.puml
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tpdf /data/c4-component.puml
	@echo "PDF diagrams generated successfully with Docker!"

# Generate all formats using Docker
.PHONY: docker-all
docker-all: docker-png docker-svg

# Clean generated files
.PHONY: clean
clean:
	@echo "Cleaning generated diagram files..."
	@sudo rm -f *.png *.svg *.pdf 2>/dev/null || true
	@echo "Generated files cleaned!"

# Generate specific diagram (local PlantUML)
.PHONY: context
context:
	@plantuml -tsvg diagrams/c4-context.puml

.PHONY: container
container:
	@plantuml -tsvg diagrams/c4-container.puml

.PHONY: component
component:
	@plantuml -tsvg diagrams/c4-component.puml

# Generate specific diagram using Docker
.PHONY: docker-context
docker-context:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/c4-context.puml

.PHONY: docker-container
docker-container:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/c4-container.puml

.PHONY: docker-component
docker-component:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/c4-component.puml

# Check if PlantUML is installed
.PHONY: check
check:
	@which plantuml > /dev/null || (echo "PlantUML not found. Use Docker commands or install PlantUML locally." && exit 1)
	@echo "PlantUML is installed and ready to use!"

# Check if Docker is available
.PHONY: check-docker
check-docker:
	@which docker > /dev/null || (echo "Docker not found. Please install Docker." && exit 1)
	@echo "Docker is available and ready to use!"

# Validate PlantUML diagrams syntax (local PlantUML)
.PHONY: validate
validate:
	@echo "Validating PlantUML diagrams syntax..."
	@plantuml -checkonly diagrams/*.puml
	@echo "All PlantUML diagrams are valid!"

# Validate PlantUML diagrams syntax using Docker
.PHONY: validate-docker
validate-docker:
	@echo "Validating PlantUML diagrams syntax with Docker..."
	@docker run --rm -v $(PWD):/data plantuml/plantuml -checkonly /data/c4-context.puml
	@docker run --rm -v $(PWD):/data plantuml/plantuml -checkonly /data/c4-container.puml
	@docker run --rm -v $(PWD):/data plantuml/plantuml -checkonly /data/c4-component.puml
	@echo "All PlantUML diagrams are valid!"

# Help target
.PHONY: help
help:
	@echo "Available targets:"
	@echo ""
	@echo "Local PlantUML (requires PlantUML installation):"
	@echo "  all       - Generate SVG diagrams (default)"
	@echo "  png       - Generate PNG diagrams"
	@echo "  svg       - Generate SVG diagrams (recommended)"
	@echo "  pdf       - Generate PDF diagrams"
	@echo "  context   - Generate only context diagram"
	@echo "  container - Generate only container diagram"
	@echo "  component - Generate only component diagram"
	@echo ""
	@echo "Docker-based (no local PlantUML installation required):"
	@echo "  docker-png       - Generate PNG diagrams with Docker"
	@echo "  docker-svg       - Generate SVG diagrams with Docker (recommended)"
	@echo "  docker-pdf       - Generate PDF diagrams with Docker"
	@echo "  docker-all       - Generate PNG and SVG with Docker"
	@echo "  docker-context   - Generate only context diagram with Docker"
	@echo "  docker-container - Generate only container diagram with Docker"
	@echo "  docker-component - Generate only component diagram with Docker"
	@echo ""
	@echo "Utilities:"
	@echo "  clean        - Remove generated files"
	@echo "  check        - Check if PlantUML is installed locally"
	@echo "  check-docker - Check if Docker is available"
	@echo "  validate     - Validate PlantUML diagram syntax (local)"
	@echo "  validate-docker - Validate PlantUML diagram syntax (Docker)"
	@echo "  help         - Show this help message"
