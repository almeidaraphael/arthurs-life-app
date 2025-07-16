# Tasks Folder - README

## Purpose
This folder contains individual markdown files for each task, along with an index file that maintains a structured record of all tasks.

## Current Status
**EMPTY** - No tasks exist because no implementation plans have been created yet.

## Memory Bank Policy
According to the Memory Bank instructions, **ALL tasks MUST be derived from implementation plans** in `/docs/implementation-plans/`. 

### Error Conditions
- **CANNOT** create task without corresponding implementation plan
- **CANNOT** edit PRDs or implementation plans from Memory Bank  
- **MUST** reference source documents in every task
- **MUST** follow template standards for all document types

### Required Workflow
1. **Need new feature?** → Create PRD following `prd-creator.chatmode.md`
2. **Need implementation details?** → Create Implementation Plan following `implementation-plan.chatmode.md`  
3. **Ready to execute?** → Create Task in Memory Bank referencing the Implementation Plan

## Current Situation
- **Implementation Plans Directory**: `/docs/implementation-plans/` is **EMPTY**
- **Available PRDs**: Multiple PRDs exist in `/docs/product-requirements-documents/`
- **Existing Implementation**: Substantial code exists (294 Kotlin files, 58 test files) but lacks proper task tracking structure

## Next Steps
1. Create implementation plans in `/docs/implementation-plans/` following proper templates
2. Once implementation plans exist, create corresponding tasks in this folder
3. Each task file must reference its source implementation plan and PRD

## File Structure
When tasks are created, they will follow this structure:
- `_index.md` - Master list of all tasks with IDs, names, and current statuses
- `TASKID-taskname.md` - Individual files for each task (e.g., `TASK001-implement-login.md`)

---
**Last Updated:** 2025-07-15
