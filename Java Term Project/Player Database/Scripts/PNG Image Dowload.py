import requests


def downloadImage(url, file_path, file_name):
    response = requests.get(url)
    full_path = file_path + file_name + '.png'
    file = open(full_path, 'wb')
    file.write(response.content)
    file.close()

url = 'https://cdn.soccerwiki.org/images/player/2772.png'
file_name = 'Messi'

downloadImage(url, 'E:\\1-2\\CSE108\\Offlines Repo\\CSE108-Offlines\\Java Term Project\\Player Database\\Club Logo\\', file_name)