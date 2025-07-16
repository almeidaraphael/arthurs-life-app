# Help target
.PHONY: help
help:
	@echo "Available targets:"
	@echo "  all            - Generate all SVG diagrams with Docker (default)"
	@echo "  docker-svg     - Generate SVG diagrams for all .puml files with Docker"
	@echo "  docker-png     - Generate PNG diagrams for all .puml files with Docker"
	@echo "  docker-pdf     - Generate PDF diagrams for all .puml files with Docker"
	@echo "  docker-all     - Generate all formats (SVG, PNG, PDF) with Docker"
	@echo "  docker-context - Generate only context diagram (SVG) with Docker"
	@echo "  docker-container - Generate only container diagram (SVG) with Docker"
	@echo "  docker-component - Generate only component diagram (SVG) with Docker"
	@echo "  docker-theme   - Generate only theme system architecture diagram (SVG) with Docker"
	@echo "  docker-security - Generate only security architecture diagram (SVG) with Docker"
	@echo "  docker-database - Generate only database ERD diagram (SVG) with Docker"
	@echo "  docker-auth    - Generate only authentication flow diagram (SVG) with Docker"
	@echo "  docker-token   - Generate only token economy state machine diagram (SVG) with Docker"
	@echo "  docker-achievement - Generate only achievement unlock sequence diagram (SVG) with Docker"
	@echo "  docker-onboarding - Generate only onboarding flow diagram (SVG) with Docker"
	@echo "  clean          - Remove all generated diagram files (*.png, *.svg, *.pdf)"
	@echo ""
	@echo "Update PUML_FILES in this Makefile if you add new diagrams."
# List of all .puml files (update this list if new diagrams are added)
PUML_FILES = \
  c4-context.puml \
  c4-container.puml \
  c4-component.puml \
  sequence-task-completion.puml \
  accessibility-architecture.puml \
  theme-system-architecture.puml \
  security-architecture.puml \
  database-entity-relationship.puml \
  user-authentication-flow.puml \
  token-economy-state-machine.puml \
  achievement-unlock-sequence.puml \
  onboarding-flow.puml \
  i18n-l10n-architecture.puml \
  reward-suggestion-system-architecture.puml \
  navigation-bar-architecture.puml \
  data-management-privacy-architecture.puml \
  family-setup-onboarding-flow.puml
# Makefile for generating PlantUML diagrams


# Default target
.PHONY: all
all: docker-svg

docker-png:
	@echo "Generating PNG diagrams with Docker..."
	@for f in $(PUML_FILES); do \
		docker run --rm -v $(PWD):/data plantuml/plantuml -tpng /data/$$f; \
	done
	@echo "PNG diagrams generated successfully with Docker!"

docker-pdf:
	@echo "Generating PDF diagrams with Docker..."
	@for f in $(PUML_FILES); do \
		docker run --rm -v $(PWD):/data plantuml/plantuml -tpdf /data/$$f; \
	done
	@echo "PDF diagrams generated successfully with Docker!"
# Generate SVG images for all diagrams using Docker
.PHONY: docker-svg
docker-svg:
	@echo "Generating SVG diagrams with Docker..."
	@for f in $(PUML_FILES); do \
		docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/$$f; \
	done
	@echo "SVG diagrams generated successfully with Docker!"

# Generate all formats using Docker
docker-all: docker-png docker-svg docker-pdf
docker-all: docker-png docker-svg docker-pdf
clean:
	@echo "Cleaning generated diagram files..."
	rm -f *.png *.svg *.pdf 2>/dev/null || true
	@echo "Generated files cleaned!"

docker-context:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/c4-context.puml

docker-container:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/c4-container.puml

docker-component:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/c4-component.puml

docker-theme:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/theme-system-architecture.puml

docker-security:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/security-architecture.puml

docker-database:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/database-entity-relationship.puml

docker-auth:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/user-authentication-flow.puml

docker-token:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/token-economy-state-machine.puml

docker-achievement:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/achievement-unlock-sequence.puml

docker-onboarding:
	@docker run --rm -v $(PWD):/data plantuml/plantuml -tsvg /data/onboarding-flow.puml

