import shutil
import requests
from bs4 import BeautifulSoup
import zipfile
import os, io
from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)


cookies = {
    'PHPSESSID': '899f675e53875c289985a06733bde6c0',
    'cf_clearance': '.oYnMsgPyjezHt.xiacEkLTIvZXCmJerOmFzngB3GZo-1757241079-1.2.1.1-MFhr7i4ARHYmr3KRHigWB4nwnPLH0fpnex0wbsgieFIbMzQPYHRyekbxiNFNNT4HBpuyEjviJxnJw393VWYxVsgNUKIbdGGe96CGNDLuzKkJ2B6cgtI6lo2QA8FhIhxNynt8oL20LskCVReZY4cCkvU8MJiB3368gz7Ra6lpL5_eN7U0TbBrcPk9qhf40Ucj3VuT9aOfDtWRL9une6j9DaYe5IWM8qDD_2HoKtI4vBQ',
}

headers = {
    'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8',
    'accept-language': 'en-GB,en;q=0.5',
    'cache-control': 'no-cache',
    'pragma': 'no-cache',
    'priority': 'u=0, i',
    'referer': 'https://www.baiscope.lk/my-fault-aka-culpa-mia-2023-sinhala-subtitles/',
    'sec-ch-ua': '"Chromium";v="140", "Not=A?Brand";v="24", "Brave";v="140"',
    'sec-ch-ua-mobile': '?0',
    'sec-ch-ua-platform': '"Windows"',
    'sec-fetch-dest': 'document',
    'sec-fetch-mode': 'navigate',
    'sec-fetch-site': 'same-origin',
    'sec-fetch-user': '?1',
    'sec-gpc': '1',
    'upgrade-insecure-requests': '1',
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',
    # 'cookie': 'PHPSESSID=899f675e53875c289985a06733bde6c0; cf_clearance=.oYnMsgPyjezHt.xiacEkLTIvZXCmJerOmFzngB3GZo-1757241079-1.2.1.1-MFhr7i4ARHYmr3KRHigWB4nwnPLH0fpnex0wbsgieFIbMzQPYHRyekbxiNFNNT4HBpuyEjviJxnJw393VWYxVsgNUKIbdGGe96CGNDLuzKkJ2B6cgtI6lo2QA8FhIhxNynt8oL20LskCVReZY4cCkvU8MJiB3368gz7Ra6lpL5_eN7U0TbBrcPk9qhf40Ucj3VuT9aOfDtWRL9une6j9DaYe5IWM8qDD_2HoKtI4vBQ',
}

API_KEY = "sk_live_0ba472111b616d0c9e44dc933ae1416381e520af3f102eb531b0041770c51cac"
UPLOADTHING_URL = "https://api.uploadthing.com/v6/uploadFiles"

def request_presigned_url(file_name, file_size, file_type):
    headers = {
        "X-Uploadthing-Api-Key": API_KEY,
        "X-Uploadthing-Version": "6.4.0",
        "X-Uploadthing-FE-Package": "python-script",
        "X-Uploadthing-BE-Adapter": "python-requests"
    }
    
    data = {
        "files": [
            {
                "name": file_name,
                "size": file_size,
                "type": file_type
            }
        ]
    }

    response = requests.post(UPLOADTHING_URL, json=data, headers=headers)
    response.raise_for_status()
    return response.json()["data"][0]  # Get the first file's presigned info

def upload_file_to_presigned(file_path):
    if not os.path.exists(file_path):
        print("File not found!")
        return None
    
    file_name = os.path.basename(file_path)
    file_size = os.path.getsize(file_path)
    file_type = "application/octet-stream"  # You can customize based on file

    presigned_data = request_presigned_url(file_name, file_size, file_type)
    
    # Presigned upload URL
    upload_url = presigned_data["url"]
    
    with open(file_path, "rb") as f:
        files = {"file": (file_name, f)}
        # Some presigned URLs may require extra fields
        fields = presigned_data.get("fields", {})
        response = requests.post(upload_url, data=fields, files=files)
        response.raise_for_status()
    
    print("File uploaded successfully!")
    return presigned_data.get("fileUrl") or presigned_data.get("url")

def download_subtitles(name):
    params = {
        's': name,
    }

    response = requests.get('https://www.baiscope.lk/', params=params, cookies=cookies, headers=headers)
    soup = BeautifulSoup(response.text, 'html.parser')
    #print(soup.prettify())
    #get first elementor-post__thumbnail__link
    link = soup.select_one('.elementor-post__thumbnail__link')
    try:
        print(link['href'])
        r = requests.get(link['href'], cookies=cookies, headers=headers)
        soup2 = BeautifulSoup(r.text, 'html.parser')
        download_link = soup2.select_one('.dlm-buttons-button-baiscopebutton')
        print(download_link['href'])
        # Download the subtitles
        download_response = requests.get(download_link['href'], cookies=cookies, headers=headers)
        #print(download_response.headers)
        #{'Date': 'Sun, 07 Sep 2025 10:50:57 GMT', 'Content-Type': 'application/zip', 'Transfer-Encoding': 'chunked', 'Connection': 'keep-alive', 'Server': 'cloudflare', 'Nel': '{"report_to":"cf-nel","success_fraction":0.0,"max_age":604800}', 'Expires': 'Thu, 19 Nov 1981 08:52:00 GMT', 'Pragma': 'no-cache', 'WPO-Cache-Status': 'not cached', 'WPO-Cache-Message': "In the settings, caching is disabled for matches for one of the current request's GET parameters", 'Content-Disposition': "attachment; filename*=UTF-8''Moana-2-2024-Sinhala-Subtitles.zip;", 'X-Robots-Tag': 'noindex, nofollow', 'Content-Description': 'File Transfer', 'Content-Transfer-Encoding': 'binary', 'Cache-Control': 'no-store, no-cache, must-revalidate, no-transform, max-age=0', 'Accept-Ranges': 'bytes', 'X-DLM-Filesize': '41685', 'cf-cache-status': 'DYNAMIC', 'Report-To': '{"group":"cf-nel","max_age":604800,"endpoints":[{"url":"https://a.nel.cloudflare.com/report/v4?s=%2B26RVPqO3n2iye2XS61oDZ9qBonv%2BM9%2BQlLUe2x%2BSPzRcEjXcfl284nXitMRhpmqfO0npNzn6%2B10EH9zUd6adfks08C%2F1DUe%2BAZ9K9N18faFeDGBTZk%2BL8oYYg%3D%3D"}]}', 'Set-Cookie': 'wp-dlm_cookie=4b334e30f299ff356f92a96c7b1cb0fe; HttpOnly; Secure; Path=/; Max-Age=60; Expires=Sun, 07 Sep 2025 10:51:57 GMT', 'CF-RAY': '97b5a5ec0a0dfdf2-SIN', 'alt-svc': 'h3=":443"; ma=86400'}
        file_name = download_response.headers.get('Content-Disposition', '').split('filename*=UTF-8\'\'')[-1]
        file_name = file_name.split(';')[0]
        print(file_name)
        with open(file_name, 'wb') as f:
            f.write(download_response.content)
        print("Subtitles downloaded successfully.")
        #unzip the file
        with zipfile.ZipFile(io.BytesIO(download_response.content)) as z:
            z.extractall("subtitles")
        os.remove(file_name)
        print("Subtitles extracted successfully.")
        #return the first .srt file in the extracted files and remove the rest
        srt_file = None
        for root, dirs, files in os.walk("subtitles"):
            for file in files:
                if file.endswith(".srt"):
                    srt_file = os.path.join(root, file)
        #remove subtitles folder delete
        if srt_file:
            print("Subtitle file:", srt_file)
            srt_file =  upload_file_to_presigned(srt_file)
        else:
            print("No .srt file found.")
        shutil.rmtree("subtitles")
        return srt_file

    except Exception as e:
        print("No link found:", e)


@app.route('/download_subtitles', methods=['GET'])
def handle_download_subtitles():
    name = request.args.get('name')
    if not name:
        return jsonify({"error": "Name parameter is required"}), 400
    
    subtitle_url = download_subtitles(name)
    if subtitle_url:
        return jsonify({"subtitle_url": subtitle_url}), 200
    else:
        return jsonify({"error": "Subtitles not found"}), 404