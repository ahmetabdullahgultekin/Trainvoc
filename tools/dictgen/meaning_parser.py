"""Parser for the packed Turkish "meaning" strings in the legacy word data.

The legacy data stores all senses of an English word in one string, e.g.:

    "(1) (birini) terk etmek (= leave) (2) bir şeyden vazgeçmek (= give up)"

Markup handled here:
  (1) (2) ...   numbered senses
  (= xxx)       English synonym hint for the headword
  (* xxx)       inline example fragment (dropped)
  (xxx)         any other parenthetical -> usage note for the sense
  ***  xxx      trailing word-level note (related words etc.)
  ---           placeholder token (dropped)
  ,  /          separators between Turkish lemmas within one sense
  !             trailing emphasis marker (dropped)

A Kotlin mirror of this parser lives in the Android app
(database/seed/MeaningParser.kt); both are tested against the shared
fixture file fixtures/parser_cases.json. Keep them in sync.
"""

from __future__ import annotations

import re
from dataclasses import dataclass, field

_NUMBER_MARK = re.compile(r"\(\s*\d+\s*\)")
_SYN_HINT = re.compile(r"\(\s*=\s*([^)]*)\)")
_EXAMPLE = re.compile(r"\(\s*\*[^)]*\)|\(\s*\*.*$")
_PAREN_NOTE = re.compile(r"\(([^)]*)\)")


@dataclass
class Sense:
    lemmas: list[str] = field(default_factory=list)
    syn_hints: list[str] = field(default_factory=list)
    note: str | None = None


@dataclass
class ParsedMeaning:
    senses: list[Sense] = field(default_factory=list)
    word_note: str | None = None


def turkish_lower(s: str) -> str:
    """Turkish-aware lowercasing (İ->i, I->ı); str.lower() is wrong for tr."""
    return s.replace("İ", "i").replace("I", "ı").lower()


def _clean_lemma(s: str) -> str:
    s = s.replace("---", " ").strip()
    s = re.sub(r"\s+", " ", s)
    return s.strip(" .!?;:-")


def _parse_sense(text: str) -> Sense:
    sense = Sense()

    def grab_hint(m: re.Match) -> str:
        hint = m.group(1).strip()
        if hint:
            sense.syn_hints.append(hint)
        return " "

    text = _SYN_HINT.sub(grab_hint, text)
    text = _EXAMPLE.sub(" ", text)

    notes: list[str] = []

    def grab_note(m: re.Match) -> str:
        note = m.group(1).strip()
        if note:
            notes.append(note)
        return " "

    text = _PAREN_NOTE.sub(grab_note, text)
    if notes:
        sense.note = "; ".join(notes)

    for part in re.split(r"[,/]", text):
        lemma = _clean_lemma(part)
        if lemma:
            sense.lemmas.append(lemma)
    return sense


def parse_meaning(raw: str) -> ParsedMeaning:
    result = ParsedMeaning()
    text = raw.strip()
    if not text:
        return result

    if "***" in text:
        text, _, note = text.partition("***")
        note = note.strip()
        if note:
            result.word_note = note

    chunks = _NUMBER_MARK.split(text)
    # With "(1) a (2) b" the split yields ["", " a ", " b "]; without
    # markers it yields [text].
    for chunk in chunks:
        if not chunk.strip():
            continue
        sense = _parse_sense(chunk)
        if sense.lemmas:
            result.senses.append(sense)
    return result
