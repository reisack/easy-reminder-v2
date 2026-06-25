#!/usr/bin/env python3
"""Report missed JaCoCo branches and lines from Easy Reminder unit coverage."""

from __future__ import annotations

import argparse
import sys
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path


DEFAULT_REPORT = Path(
    "app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"
)


@dataclass(frozen=True)
class Gap:
    path: str
    line: int
    missed_branches: int
    covered_branches: int
    missed_instructions: int
    covered_instructions: int

    @property
    def kind(self) -> str:
        return "branch" if self.missed_branches else "line"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="List partially covered branches and uncovered lines in a JaCoCo XML report."
    )
    parser.add_argument(
        "report",
        nargs="?",
        type=Path,
        default=DEFAULT_REPORT,
        help=f"JaCoCo XML path (default: {DEFAULT_REPORT})",
    )
    parser.add_argument(
        "--all",
        action="store_true",
        help="Include fully missed non-branch lines after branch gaps.",
    )
    return parser.parse_args()


def percentage(covered: int, missed: int) -> str:
    total = covered + missed
    return "n/a" if total == 0 else f"{covered * 100 / total:.1f}%"


def read_report(report: Path) -> tuple[list[Gap], list[str]]:
    root = ET.parse(report).getroot()
    gaps: list[Gap] = []
    summaries: list[str] = []

    for package in root.findall("package"):
        package_name = package.get("name", "")
        for source in package.findall("sourcefile"):
            source_name = source.get("name", "")
            source_path = f"{package_name}/{source_name}".replace("\\", "/")

            for line in source.findall("line"):
                missed_branches = int(line.get("mb", "0"))
                covered_branches = int(line.get("cb", "0"))
                missed_instructions = int(line.get("mi", "0"))
                covered_instructions = int(line.get("ci", "0"))

                if missed_branches > 0 or (
                    missed_instructions > 0 and covered_instructions == 0
                ):
                    gaps.append(
                        Gap(
                            path=source_path,
                            line=int(line.get("nr", "0")),
                            missed_branches=missed_branches,
                            covered_branches=covered_branches,
                            missed_instructions=missed_instructions,
                            covered_instructions=covered_instructions,
                        )
                    )

            counters = {counter.get("type"): counter for counter in source.findall("counter")}
            branch = counters.get("BRANCH")
            line_counter = counters.get("LINE")
            branch_missed = int(branch.get("missed", "0")) if branch is not None else 0
            branch_covered = int(branch.get("covered", "0")) if branch is not None else 0
            line_missed = (
                int(line_counter.get("missed", "0")) if line_counter is not None else 0
            )
            line_covered = (
                int(line_counter.get("covered", "0")) if line_counter is not None else 0
            )

            if branch_missed or line_missed:
                summaries.append(
                    f"{source_path}: branches "
                    f"{percentage(branch_covered, branch_missed)} "
                    f"({branch_covered} covered, {branch_missed} missed), lines "
                    f"{percentage(line_covered, line_missed)} "
                    f"({line_covered} covered, {line_missed} missed)"
                )

    gaps.sort(
        key=lambda gap: (
            0 if gap.missed_branches else 1,
            -gap.missed_branches,
            gap.path,
            gap.line,
        )
    )
    summaries.sort()
    return gaps, summaries


def main() -> int:
    args = parse_args()
    report = args.report.resolve()

    if not report.is_file():
        print(f"JaCoCo report not found: {report}", file=sys.stderr)
        print("Run .\\gradlew.bat jacocoTestReport first.", file=sys.stderr)
        return 2

    try:
        gaps, summaries = read_report(report)
    except ET.ParseError as error:
        print(f"Invalid JaCoCo XML: {error}", file=sys.stderr)
        return 2

    branch_gaps = [gap for gap in gaps if gap.missed_branches]
    line_gaps = [gap for gap in gaps if not gap.missed_branches]

    if branch_gaps:
        print("Missed branch outcomes:")
        for gap in branch_gaps:
            print(
                f"  {gap.path}:{gap.line} - "
                f"{gap.missed_branches} missed, {gap.covered_branches} covered"
            )
    else:
        print("No missed branch outcomes found.")

    if args.all:
        print("\nFully missed executable lines:")
        if line_gaps:
            for gap in line_gaps:
                print(
                    f"  {gap.path}:{gap.line} - "
                    f"{gap.missed_instructions} missed instructions"
                )
        else:
            print("  None")

    if summaries:
        print("\nFiles with remaining coverage gaps:")
        for summary in summaries:
            print(f"  {summary}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
