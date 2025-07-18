# Documentation Guidelines - Technical Documentation

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

Comprehensive guidelines for creating, maintaining, and standardizing technical documentation in the LemonQwest project.

## 📋 Document Overview

### Purpose

Establish consistent standards for technical documentation to ensure clarity, maintainability, and ease of use for all contributors and users.

### Audience

- **Primary**: Contributors and developers writing technical documentation
- **Secondary**: Technical reviewers and documentation maintainers
- **Prerequisites**: Basic understanding of Markdown and project structure

### Scope

Covers technical documentation standards for the `/docs/` directory. For planning documentation standards, see [Planning Documentation Standards](../planning/FEATURE_DOCUMENTATION_STANDARDS.md).

## 🎯 Quick Reference

### Key Information

- **Summary**: Standards for technical documentation in `/docs/` directory
- **Related**: [Template](TEMPLATE.md), [Planning Standards](../planning/FEATURE_DOCUMENTATION_STANDARDS.md)

### Common Tasks

- [Create new documentation](#creating-new-documentation)
- [Update existing documentation](#updating-existing-documentation)
- [Review documentation](#review-process)
- [Maintain documentation](#maintenance-procedures)

## 📖 Documentation Standards

### File Structure and Naming

#### File Naming Conventions

- **Format**: `kebab-case.md` (e.g., `development-tools-guide.md`)
- **Descriptive**: Use clear, descriptive names that indicate content
- **Consistent**: Follow established patterns in the directory
- **Avoid**: Spaces, special characters, or version numbers in filenames

#### Directory Organization

```
docs/
├── README.md              # Documentation hub
├── TEMPLATE.md            # Template for new docs
├── DOCUMENTATION_GUIDELINES.md  # This file
├── [category]-[topic].md  # Technical documentation
└── diagrams/              # Visual documentation
    ├── README.md          # Diagrams index
    └── *.puml, *.svg      # Diagram files
```

### Document Structure

#### Required Header Format

```markdown
# [Document Title] - Technical Documentation

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

Brief description of what this document covers and who should read it.
```

#### Required Sections

All technical documents must include:

1. **📋 Document Overview** - Purpose, audience, scope
2. **🎯 Quick Reference** - Key information and common tasks
3. **📖 Main Content** - Core documentation content
4. **🔗 Integration Points** - Dependencies and related features
8. **📚 Additional Resources** - Related documentation and external links
9. **📝 Contributing** - How to contribute to this document

#### Optional Sections

Include when relevant:

- **🎯 Quick Start Guide** - For setup/getting started documents
- **🔍 Troubleshooting** - For technical guides
- **⚙️ Configuration** - For setup and tool documentation
- **📝 Examples** - For implementation guides
- **🔒 Security Considerations** - For security-related content

### Content Guidelines

#### Writing Style

- **Clear and Concise**: Use simple, direct language
- **Active Voice**: "Use the template" not "The template should be used"
- **Present Tense**: Describe features as they currently exist
- **Consistent Terminology**: Use same terms throughout all documents

#### Technical Detail Level

- **Implementation Focus**: Include code examples, configuration details
- **Architecture Details**: Explain technical decisions and patterns
- **Tool-Specific Information**: Include setup, usage, and troubleshooting
- **Cross-References**: Link to related technical and planning documentation

#### Formatting Standards

- **Headings**: Use emoji prefixes for section headings (📋, 🎯, 📖, etc.)
- **Code Blocks**: Use appropriate language identifiers
- **Lists**: Use consistent bullet points and numbering
- **Tables**: Include headers and consistent formatting
- **Links**: Use descriptive link text, not "click here"

### Navigation Standards

#### Top Navigation

```markdown
[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)
```

#### Bottom Navigation

```markdown
**Navigation**: [🏠 Docs Hub](README.md) | [🏠 Main README](../README.md) | [📋 Planning](../planning/README.md)
```

#### Cross-References

- **Internal Links**: Use relative paths within project
- **External Links**: Include full URLs with descriptive text
- **Planning Links**: Link to relevant planning documents
- **Bidirectional**: Ensure related documents link to each other

## 🔗 Integration Points

### Dependencies

- **Template**: All new documents must use [TEMPLATE.md](TEMPLATE.md)
- **Planning Standards**: Coordinate with [Planning Documentation Standards](../planning/FEATURE_DOCUMENTATION_STANDARDS.md)
- **Main README**: Update navigation tables when adding new documentation

### Related Documentation

- **Planning Documents**: Link to relevant feature specifications
- **Code Examples**: Reference actual implementation files
- **External Resources**: Include links to official documentation

## 📚 Additional Resources

### Internal Documentation

- [Documentation Template](TEMPLATE.md)
- [Planning Documentation Standards](../planning/FEATURE_DOCUMENTATION_STANDARDS.md)
- [Contributing Guide](contributing.md)

### External Resources

- [Markdown Guide](https://www.markdownguide.org/)
- [GitHub Flavored Markdown](https://github.github.com/gfm/)
- [Technical Writing Best Practices](https://developers.google.com/tech-writing)

## 📝 Creating New Documentation

### Step-by-Step Process

1. **Copy the Template**: Start with [TEMPLATE.md](TEMPLATE.md)
2. **Choose Appropriate Filename**: Use kebab-case naming
3. **Complete All Required Sections**: Don't skip template sections
4. **Add Navigation Links**: Update [docs/README.md](README.md) and main README
5. **Review and Test**: Ensure all links work and content is clear
6. **Submit for Review**: Follow established review process

### Before Creating New Documentation

- **Check Existing Docs**: Avoid duplication
- **Identify Target Audience**: Determine who will use this documentation
- **Plan Content Structure**: Outline main sections and topics
- **Gather Resources**: Collect examples, links, and references

## 📝 Updating Existing Documentation

### Update Process

1. **Review Current Content**: Check for outdated information
2. **Update Implementation Status**: Reflect current development state
3. **Verify Cross-References**: Ensure all links work correctly
4. **Update Navigation**: Modify hub documentation if needed
5. **Test Changes**: Validate all updates work as expected

## 📝 Review Process

### Review Criteria

- **Template Compliance**: Follows established structure
- **Content Quality**: Clear, accurate, and useful
- **Technical Accuracy**: Correct implementation details
- **Style Consistency**: Matches established guidelines
- **Link Validation**: All references work correctly

### Review Steps

1. **Technical Review**: Verify technical accuracy
2. **Editorial Review**: Check grammar, clarity, and consistency
3. **Link Validation**: Test all internal and external links
4. **Template Compliance**: Ensure structure matches template
5. **Final Approval**: Approve for publication

---

## 📝 Contributing

### How to Contribute

1. **Follow Guidelines**: Use this document as reference
2. **Use Template**: Start with [TEMPLATE.md](TEMPLATE.md)
3. **Test Changes**: Validate all updates work correctly
4. **Submit for Review**: Follow established review process

### Review Process

1. **Self-Review**: Check against these guidelines
2. **Peer Review**: Have another contributor review
3. **Technical Review**: Ensure technical accuracy
4. **Final Approval**: Approve for publication

### Style Guidelines

- Use clear, concise language
- Follow established formatting standards
- Include appropriate cross-references
- Maintain consistent terminology

---

**Navigation**: [🏠 Docs Hub](README.md) | [🏠 Main README](../README.md) | [📋 Planning](../planning/README.md)
