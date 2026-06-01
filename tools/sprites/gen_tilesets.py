from PIL import Image, ImageDraw
import os

def gen_tileset(name, colors):
    os.makedirs(f'assets/tiles/{name}', exist_ok=True)

    sheet = Image.new('RGBA', (16 * 8, 16 * 4), (0, 0, 0, 0))
    draw = ImageDraw.Draw(sheet)

    for i, color in enumerate(colors):
        x = (i % 8) * 16
        y = (i // 8) * 16
        draw.rectangle([x, y, x + 15, y + 15], fill=color)
        draw.rectangle([x, y, x + 15, y + 15], outline=(0, 0, 0, 50))

    sheet.save(f'assets/tiles/{name}/{name}_tileset.png')
    print(f"Generated {name}_tileset.png")

if __name__ == "__main__":
    # Forest: Greens and browns
    gen_tileset('forest', [(34, 139, 34), (0, 100, 0), (139, 69, 19), (101, 67, 33)])
    # Wastes: Oranges and grays
    gen_tileset('wastes', [(205, 133, 63), (160, 82, 45), (128, 128, 128), (105, 105, 105)])
    # Fortress: Cold grays and hazard yellows
    gen_tileset('fortress', [(112, 128, 144), (47, 79, 79), (255, 215, 0), (0, 0, 0)])
