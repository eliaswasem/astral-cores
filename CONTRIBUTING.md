# Contributing

## General

The following guidelines apply to **all contributions**.

- Reuse existing functions, utilities, abstractions, APIs, and systems whenever possible.
- Do not implement functionality that already exists elsewhere in the project.
- Follow the existing architecture, design decisions, interfaces, and conventions.
- Ensure all changes are functionally correct and properly tested.
- Do not introduce security vulnerabilities or unsafe implementations.
- Do not introduce memory leaks or other resource-management issues.
- Consider CPU, memory, I/O, concurrency, and scalability implications.
- Avoid unnecessary complexity, duplication, and abstractions.
- Document code where appropriate using clear, descriptive English comments. Comments should explain non-obvious logic, decisions, constraints, or behavior rather than restating what the code already says.
- Keep documentation up to date when behavior, APIs, or architecture change.
- Review changes carefully before submitting them and take full responsibility for the resulting implementation.

## AI Usage

AI tools may be used for assistance, but should **only be used when necessary**. Code should preferably be written without AI assistance whenever possible.

If AI is used, the author remains fully responsible for the resulting code.

**Do not blindly copy and paste the first solution provided by an AI tool.** AI output should be treated as a starting point that must be evaluated, adapted, and tested.

Before asking AI to implement or modify something, look at the relevant existing code and provide the AI with sufficient context about the project. This includes the relevant architecture, APIs, dependencies, conventions, and existing implementations.

When using AI, follow the requirements in the **General** section and verify that the generated solution fits the existing codebase rather than using it unchanged.

AI assistance does not replace understanding the codebase, code review, testing, or responsibility for the final implementation.

## PRs & Commits

PRs and commits should focus on **one thing at a time**.

A PR or commit should have one clear, logical purpose and should contain only changes directly related to that purpose.

Do not combine unrelated:

- features,
- bug fixes,
- refactors,
- dependency updates,
- formatting changes,
- documentation changes.

If changes are independent of each other, they should be split into separate PRs or commits.

PRs and commits should use a Conventional Commits-style prefix:

| Prefix | Purpose | Example |
|---|---|---|
| `feat` | New functionality | `feat: add chrono core active ability` |
| `fix` | Bug fix | `fix: prevent duplicate core activation` |
| `refactor` | Code restructuring | `refactor: simplify core activation logic` |
| `perf` | Performance improvement | `perf: reduce core tick overhead` |
| `docs` | Documentation changes | `docs: document core creation` |
| `test` | Test changes | `test: add chrono core tests` |
| `build` | Build system or dependency changes | `build: update Fabric dependencies` |
| `ci` | CI/CD changes | `ci: add mod test workflow` |
| `chore` | Other maintenance | `chore: update development tooling` |
| `revert` | Revert a previous change | `revert: feat: add chrono core active ability` |

For **breaking changes**, suffix the type with `!`:

```text
feat!: change the Core API
fix!: change command argument handling
refactor!: remove the legacy CoreFactory interface
perf!: change core tick behavior
build!: update the required Minecraft version
```
The ! suffix can be used with any type and indicates that the change introduces a breaking change.

PR and commit titles should follow the same convention.
