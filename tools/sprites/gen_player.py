from PIL import Image, ImageDraw
import os

def create_sprite(color, size=(16, 16)):
    img = Image.new('RGBA', size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # Simple humanoid shape
    draw.rectangle([4, 4, 11, 15], fill=color) # Body
    draw.rectangle([5, 1, 10, 6], fill=(255, 224, 189)) # Head/Skin
    return img

def gen_player():
    os.makedirs('assets/sheets/player', exist_ok=True)

    # Kaelen's colors: Blue tunic, brown boots
    base_color = (0, 100, 200)

    # 4 directions (Down, Up, Left, Right) x 3 frames
    sheet = Image.new('RGBA', (16 * 3, 16 * 4), (0, 0, 0, 0))

    for row in range(4):
        for col in range(3):
            sprite = create_sprite(base_color)
            # Add some "animation" variation
            draw = ImageDraw.Draw(sprite)
            if col == 1: # Left leg up
                draw.point([4, 15], fill=(0,0,0,0))
            if col == 2: # Right leg up
                draw.point([11, 15], fill=(0,0,0,0))

            sheet.paste(sprite, (col * 16, row * 16))

    sheet.save('assets/sheets/player/kaelen_sheet.png')
    print("Generated kaelen_sheet.png")

if __name__ == "__main__":
    gen_player()
