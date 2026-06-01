from PIL import Image, ImageDraw
import os

def gen_ui():
    os.makedirs('assets/ui', exist_ok=True)
    os.makedirs('assets/fonts', exist_ok=True)

    # Dialogue Box
    box = Image.new('RGBA', (48, 48), (0, 0, 0, 200))
    draw = ImageDraw.Draw(box)
    draw.rectangle([0, 0, 47, 47], outline=(255, 255, 255, 255), width=2)
    box.save('assets/ui/dialogue_box.png')

    # Cursor
    cursor = Image.new('RGBA', (8, 8), (0, 0, 0, 0))
    draw = ImageDraw.Draw(cursor)
    draw.polygon([(0,0), (7,3), (3,7)], fill=(255, 255, 255))
    cursor.save('assets/ui/cursor.png')

    # Font (Stub)
    font = Image.new('RGBA', (128, 128), (0, 0, 0, 0))
    draw = ImageDraw.Draw(font)
    draw.text((0,0), "ABCDEFG...", fill=(255, 255, 255))
    font.save('assets/fonts/default_font.png')

    print("Generated UI assets")

if __name__ == "__main__":
    gen_ui()
