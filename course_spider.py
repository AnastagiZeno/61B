import requests
import os
from bs4 import BeautifulSoup

# Author: Bx.K
# anastagizeno@gmail.com


# Python3.6
"""
beautifulsoup4==4.6.3
bs4==0.0.1
certifi==2018.8.24
chardet==3.0.4
idna==2.7
requests==2.19.1
urllib3==1.23
"""


ROOT_PATH = os.path.join(os.path.dirname(__file__), 'hws&projs')
ROOT_URL = "https://people.eecs.berkeley.edu/~jrs/61b/hw/"
print("All files will be downloaded into >> ", ROOT_PATH)


def main():
    resp = requests.get(ROOT_URL)
    soup = BeautifulSoup(resp.content, "html.parser")
    hws = soup.find_all('a')
    hws = list(filter(lambda x: "Project" in x.text or "Homework" in x.text, hws))
    for idx, hw in enumerate(hws):
        print(">>"*30, '   ', hw.text)
        traverse(hw, ROOT_PATH, ROOT_URL)


def traverse(tag, root_path, root_url):
    root = mkdir(root_path, tag.text)
    child_url = root_url + tag.attrs['href'] + '/'
    resp = requests.get(child_url)
    soup = BeautifulSoup(resp.content, "html.parser")
    files, dirs = [], []
    for item in soup.find_all('a'):
        if 'href' not in item.attrs:
            continue
        key = item.attrs['href']
        if key[0] in ['/', '?', '&']:
            continue
        if key.endswith('.ps') or key.endswith('.pdf'):
            continue
        if key.endswith('/'):
            dirs.append(item)
        else:
            files.append(item)

    print("files: >> ", files)
    print("dirs: >> ", dirs)

    if files:
        files_downloading(files, child_url, root)

    if dirs:
        dirs_downloading(dirs, child_url, root)


def files_downloading(files, root_url, root_path):
    for file in files:
        file_name = file.text
        file_url = root_url + file.attrs['href']
        content = requests.get(file_url).content
        with open(os.path.join(root_path, file_name), 'wb') as h:
            h.write(content)


def dirs_downloading(dirs, root_url, root_path):
    for _dir in dirs:
        index(_dir, root_path, root_url)


def mkdir(path, name):
    folder = os.path.exists(os.path.join(path, name))
    if not folder:
        os.makedirs(os.path.join(path, name))
    else:
        raise RuntimeError("Dir already exists...")
    return os.path.join(path, name)


if __name__ == '__main__':
    main()
