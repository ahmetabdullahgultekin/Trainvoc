"""Fixture-driven tests for meaning_parser. Run: python3 test_parser.py"""

import json
import pathlib
import sys

from meaning_parser import parse_meaning

FIXTURES = pathlib.Path(__file__).parent / "fixtures" / "parser_cases.json"


def main() -> int:
    cases = json.loads(FIXTURES.read_text(encoding="utf-8"))["cases"]
    failures = 0
    for case in cases:
        got = parse_meaning(case["input"])
        got_senses = [
            {"lemmas": s.lemmas, "synHints": s.syn_hints, "note": s.note}
            for s in got.senses
        ]
        ok = got_senses == case["senses"] and got.word_note == case["wordNote"]
        if not ok:
            failures += 1
            print(f"FAIL {case['name']}")
            print(f"  input:    {case['input']!r}")
            print(f"  expected: {case['senses']} note={case['wordNote']!r}")
            print(f"  got:      {got_senses} note={got.word_note!r}")
        else:
            print(f"ok   {case['name']}")
    if failures:
        print(f"{failures}/{len(cases)} cases failed")
        return 1
    print(f"all {len(cases)} cases passed")
    return 0


if __name__ == "__main__":
    sys.exit(main())
