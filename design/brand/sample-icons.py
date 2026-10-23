#!/usr/bin/env python3
"""Draws the app icons of the two sample apps from the Crease mark.

Both keep the half-open Duo of design/brand/icon.svg and add what each sample is about: the recipes app
is an orange cookbook with lines of text on its pages, the photos app a violet gallery with a landscape
on one panel and thumbnails on the other.

Writes, for each sample:
  design/brand/<sample>-icon.svg                  the icon with rounded corners, for previews
  samples/<sample>/androidApp/src/main/res/...    an adaptive launcher icon and a vector for API 23 to 25
  samples/<sample>/iosApp/<App>/Assets.xcassets   the 1024 px app icon, rendered with headless Chrome

Run it from the repository root: python3 design/brand/sample-icons.py
"""

import json
import math
import pathlib
import subprocess
import tempfile

ROOT = pathlib.Path(__file__).resolve().parents[2]
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
K = 0.5523  # bezier handle length for a quarter circle
TILT = math.tan(math.radians(12))


def rounded_rect(x, y, w, h, r):
    """A rounded rectangle as a closed path of cubic beziers, so a skew keeps it exact."""
    r = min(r, w / 2, h / 2)
    c = r * K
    return [
        ("M", [(x + r, y)]),
        ("L", [(x + w - r, y)]),
        ("C", [(x + w - r + c, y), (x + w, y + r - c), (x + w, y + r)]),
        ("L", [(x + w, y + h - r)]),
        ("C", [(x + w, y + h - r + c), (x + w - r + c, y + h), (x + w - r, y + h)]),
        ("L", [(x + r, y + h)]),
        ("C", [(x + r - c, y + h), (x, y + h - r + c), (x, y + h - r)]),
        ("L", [(x, y + r)]),
        ("C", [(x, y + r - c), (x + r - c, y), (x + r, y)]),
        ("Z", []),
    ]


def circle(cx, cy, r):
    return rounded_rect(cx - r, cy - r, 2 * r, 2 * r, r)


def polygon(*points):
    return [("M", [points[0]])] + [("L", [p]) for p in points[1:]] + [("Z", [])]


def skew(path, direction):
    """skewY(±12°) around the origin, like the panels of icon.svg."""
    t = TILT * direction
    return [(cmd, [(x, y + x * t) for x, y in pts]) for cmd, pts in path]


def place(path, scale, dx, dy):
    return [(cmd, [(x * scale + dx, y * scale + dy) for x, y in pts]) for cmd, pts in path]


def data(path):
    return " ".join(cmd + " ".join(f"{x:.2f},{y:.2f}" for x, y in pts) for cmd, pts in path)


# The mark, in the coordinates of icon.svg around its centre (256, 262).
LEFT = (1, rounded_rect(-142, -128, 134, 244, 30))
RIGHT = (-1, rounded_rect(8, -128, 134, 244, 30))
ISLAND = (1, rounded_rect(-104, -122, 56, 16, 8))


def recipes():
    # An open cookbook: a title and lines of text on both pages.
    left = [rounded_rect(-122, -78, 64, 22, 11)] + [
        rounded_rect(-122, y, w, 14, 7) for y, w in ((-36, 94), (-8, 74), (20, 94), (48, 56))
    ]
    right = [rounded_rect(28, -92, 64, 22, 11)] + [
        rounded_rect(28, y, w, 14, 7) for y, w in ((-50, 94), (-22, 80), (6, 94), (34, 62))
    ]
    return {"tile": "#e8590c", "left": left, "right": right}


def photos():
    # A gallery: a landscape on the left panel, a grid of thumbnails on the right.
    left = [
        circle(-48, -62, 15),
        polygon((-128, 50), (-92, -8), (-74, 18), (-58, 0), (-22, 50)),
    ]
    right = [rounded_rect(x, y, 50, 50, 10) for y in (-78, -20) for x in (22, 80)] + [rounded_rect(22, 38, 50, 50, 10)]
    return {"tile": "#7048e8", "left": left, "right": right}


def shapes(icon, scale, dx, dy):
    """The paths of one icon as (path data, fill, opacity), from the back to the front."""
    out = []
    for (direction, panel), opacity in ((LEFT, 1), (RIGHT, 0.55)):
        out.append((data(place(skew(panel, direction), scale, dx, dy)), "#ffffff", opacity))
    direction, island = ISLAND
    out.append((data(place(skew(island, direction), scale, dx, dy)), icon["tile"], 1))
    for path in icon["left"]:
        out.append((data(place(skew(path, 1), scale, dx, dy)), icon["tile"], 1))
    for path in icon["right"]:
        out.append((data(place(skew(path, -1), scale, dx, dy)), icon["tile"], 0.8))
    return out


def svg(icon, radius):
    paths = "\n".join(
        f'  <path d="{d}" fill="{fill}"' + (f' fill-opacity="{a}"' if a != 1 else "") + "/>"
        for d, fill, a in shapes(icon, 1, 256, 262)
    )
    return (
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">\n'
        f'  <rect width="512" height="512" rx="{radius}" fill="{icon["tile"]}"/>\n{paths}\n</svg>\n'
    )


def vector(icon, size, scale, background):
    """An Android vector drawable. The adaptive foreground has no tile; the legacy icon draws its own."""
    tile = (
        f'    <path android:pathData="{data(rounded_rect(4, 4, size - 8, size - 8, 12))}" android:fillColor="{icon["tile"]}"/>\n'
        if background
        else ""
    )
    paths = "".join(
        f'    <path android:pathData="{d}" android:fillColor="{fill}"'
        + (f' android:fillAlpha="{a}"' if a != 1 else "")
        + "/>\n"
        for d, fill, a in shapes(icon, scale, size / 2, size / 2 + 6 * scale)
    )
    return (
        '<?xml version="1.0" encoding="utf-8"?>\n'
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
        f'    android:width="{size}dp"\n    android:height="{size}dp"\n'
        f'    android:viewportWidth="{size}"\n    android:viewportHeight="{size}">\n'
        f"{tile}{paths}</vector>\n"
    )


ADAPTIVE = """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
"""


def render_png(svg_text, file):
    """iOS wants a square, opaque 1024 px PNG and rounds the corners itself."""
    with tempfile.TemporaryDirectory() as tmp:
        page = pathlib.Path(tmp, "icon.html")
        page.write_text(f"<style>*{{margin:0}}svg{{display:block;width:1024px;height:1024px}}</style>{svg_text}")
        subprocess.run(
            [CHROME, "--headless", "--hide-scrollbars", f"--screenshot={file}", "--window-size=1024,1024", page.as_uri()],
            check=True,
            capture_output=True,
        )


def write(path, text):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text)
    print(path.relative_to(ROOT))


for name, app, icon in (("recipes", "RecipesApp", recipes()), ("photos", "PhotosApp", photos())):
    write(ROOT / f"design/brand/{name}-icon.svg", svg(icon, 112))

    res = ROOT / f"samples/{name}/androidApp/src/main/res"
    # 108 dp with the art inside the 66 dp the launchers always show.
    write(res / "drawable/ic_launcher_foreground.xml", vector(icon, 108, 0.16, background=False))
    write(res / "mipmap/ic_launcher.xml", vector(icon, 48, 0.12, background=True))
    write(res / "mipmap-anydpi-v26/ic_launcher.xml", ADAPTIVE)
    write(
        res / "values/ic_launcher.xml",
        '<?xml version="1.0" encoding="utf-8"?>\n<resources>\n'
        f'    <color name="ic_launcher_background">{icon["tile"].upper()}</color>\n</resources>\n',
    )

    assets = ROOT / f"samples/{name}/iosApp/{app}/Assets.xcassets"
    write(assets / "Contents.json", json.dumps({"info": {"author": "xcode", "version": 1}}, indent=2) + "\n")
    write(
        assets / "AppIcon.appiconset/Contents.json",
        json.dumps(
            {
                "images": [{"filename": "icon-1024.png", "idiom": "universal", "platform": "ios", "size": "1024x1024"}],
                "info": {"author": "xcode", "version": 1},
            },
            indent=2,
        )
        + "\n",
    )
    png = assets / "AppIcon.appiconset/icon-1024.png"
    render_png(svg(icon, 0), png)
    print(png.relative_to(ROOT))
