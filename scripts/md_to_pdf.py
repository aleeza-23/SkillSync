from pathlib import Path

from fpdf import FPDF


def clean_text(line: str) -> str:
    text = line.replace("`", "").replace("\t", "    ").strip()
    return break_long_tokens(text)


def break_long_tokens(text: str, max_token_len: int = 50) -> str:
    parts = text.split(" ")
    normalized = []
    for token in parts:
        if len(token) <= max_token_len:
            normalized.append(token)
            continue
        token = token.replace("\\", "\\ ").replace("/", "/ ")
        normalized.append(token)
    return " ".join(normalized)


def convert_markdown_to_pdf(md_path: Path, pdf_path: Path) -> None:
    lines = md_path.read_text(encoding="utf-8").splitlines()

    pdf = FPDF(format="A4")
    pdf.set_auto_page_break(auto=True, margin=15)
    pdf.add_page()

    for raw_line in lines:
        line = raw_line.rstrip()
        if not line.strip():
            pdf.ln(3)
            continue
        pdf.set_x(pdf.l_margin)

        if line.startswith("# "):
            pdf.set_font("Helvetica", "B", 16)
            try:
                pdf.multi_cell(190, 9, clean_text(line[2:]))
            except Exception:
                print(f"Failed heading line: {raw_line!r}")
                raise
            pdf.ln(2)
        elif line.startswith("## "):
            pdf.set_font("Helvetica", "B", 13)
            try:
                pdf.multi_cell(190, 8, clean_text(line[3:]))
            except Exception:
                print(f"Failed h2 line: {raw_line!r}")
                raise
            pdf.ln(1)
        elif line.startswith("### "):
            pdf.set_font("Helvetica", "B", 11)
            try:
                pdf.multi_cell(190, 7, clean_text(line[4:]))
            except Exception:
                print(f"Failed h3 line: {raw_line!r}")
                raise
        elif line.lstrip().startswith("- "):
            content = clean_text(line.strip()[2:])
            pdf.set_font("Helvetica", "", 11)
            try:
                pdf.multi_cell(190, 6, f"- {content}")
            except Exception:
                print(f"Failed bullet line: {raw_line!r}")
                raise
        else:
            pdf.set_font("Helvetica", "", 11)
            try:
                pdf.multi_cell(190, 6, clean_text(line))
            except Exception:
                print(f"Failed paragraph line: {raw_line!r}")
                raise

    pdf.output(str(pdf_path))


if __name__ == "__main__":
    project_root = Path(__file__).resolve().parents[1]
    md_file = project_root / "TESTING_DELIVERABLE_SPRINT3.md"
    pdf_file = project_root / "TESTING_DELIVERABLE_SPRINT3.pdf"
    convert_markdown_to_pdf(md_file, pdf_file)
    print(f"Created: {pdf_file}")
