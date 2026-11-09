#!/usr/bin/env python3
"""Fails when a public symbol in a binary API dump has no entry in the reference pages.

Reads the top-level public declarations (types, functions, properties) from every module's klib dump
and checks that each name appears in apps/docs/docs/reference/*.mdx. Compiler-generated names are
skipped. Names that are public but deliberately undocumented go in scripts/reference-allowlist.txt,
one per line as `Name  # reason`.
Run `python3 scripts/check-reference.py`.
"""

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DUMPS = sorted(ROOT.glob("crease*/api/*.klib.api"))
REFERENCE = ROOT / "apps/docs/docs/reference"
ALLOWLIST = ROOT / "scripts/reference-allowlist.txt"

TYPE = re.compile(r"^(?:\w+ )*(?:class|interface|object) [\w.]+/(\w+)")
FUN = re.compile(r"^(?:\w+ )*fun (?:<.*?> )?(?:\(.*?\)\.)?[\w.]+/(\w+)\(")
VAL = re.compile(r"^(?:\w+ )*val [\w.]+/(\w+)")


def public_names(dump: Path) -> set[str]:
    names = set()
    for line in dump.read_text().splitlines():
        if not line or line[0] in " /\t}":
            continue
        for pattern in (TYPE, FUN, VAL):
            match = pattern.match(line)
            if match:
                name = match.group(1)
                if "$" not in name and not name.startswith("com_"):
                    names.add(name)
                break
    return names


def allowed() -> set[str]:
    if not ALLOWLIST.exists():
        return set()
    lines = (line.split("#")[0].strip() for line in ALLOWLIST.read_text().splitlines())
    return {line for line in lines if line}


def main() -> int:
    text = "\n".join(page.read_text() for page in sorted(REFERENCE.glob("*.mdx")))
    skipped = allowed()
    missing = []
    for dump in DUMPS:
        for name in sorted(public_names(dump)):
            if name in skipped or re.search(rf"(?<![\w]){re.escape(name)}(?![\w])", text):
                continue
            missing.append(f"{dump.name.removesuffix('.klib.api')}: {name}")
    if missing:
        print("Public symbols with no entry in apps/docs/docs/reference/:")
        for entry in missing:
            print(f"  {entry}")
        print("Document them, or allowlist them with a reason in scripts/reference-allowlist.txt.")
        return 1
    print(f"Reference pages cover every public symbol in {len(DUMPS)} API dumps.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
