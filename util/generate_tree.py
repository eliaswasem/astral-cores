import os
import sys

def generate_tree(start_dir):
    output = []
    output.append("# Project Tree")
    output.append("```text")
    output.append("src/main/java/de/ep/astralcores/")

    base_path = os.path.join(
        start_dir,
        "src", "main", "java", "de", "ep", "astralcores"
    )

    if not os.path.exists(base_path):
        base_path = start_dir
        output = ["# Project Tree", "```text", os.path.basename(start_dir) + "/"]

    def _walk(current_dir, prefix=""):
        try:
            items = sorted(os.listdir(current_dir))
        except Exception:
            return

        dirs = [
            d for d in items
            if os.path.isdir(os.path.join(current_dir, d))
        ]

        files = [
            f for f in items
            if os.path.isfile(os.path.join(current_dir, f))
            and f.endswith(".java")
        ]

        all_items = dirs + files

        for i, item in enumerate(all_items):
            is_last = i == len(all_items) - 1
            path = os.path.join(current_dir, item)

            marker = "└── " if is_last else "├── "
            output.append(f"{prefix}{marker}{item}")

            if os.path.isdir(path):
                extension = "    " if is_last else "│   "
                _walk(path, prefix + extension)

    _walk(base_path)
    output.append("```")

    return "\n".join(output)


if __name__ == "__main__":
    current_directory = os.getcwd()
    tree_text = generate_tree(current_directory)

    output_dir = os.path.join(current_directory, "docs")
    os.makedirs(output_dir, exist_ok=True)

    output_file = os.path.join(output_dir, "PROJECT_TREE.md")

    with open(output_file, "w", encoding="utf-8") as f:
        f.write(tree_text)

    print(f"The file {output_file} has been successfully generated!")