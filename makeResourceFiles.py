# -*- coding: utf-8 -*-
import os
import hashlib
import json
import codecs

FILE_APK = 'menu-list-1.0.4.apk'
FILE_MENU = 'menulist-17-01'
YEAR = 2017
RESOURCE_DIR = 'resources'


class DayMenu:
    def __init__(self):
        self.lunchMenus = []
        self.dinnerMenus = []
        self.isHoliday = False
        
    def setDate(self, strDate):
        split = strDate.split('/')
        self.month = int(split[0])
        self.day = int(split[1])
        self.year = YEAR


from bs4 import BeautifulSoup
def parseHtml(fileName) :
    result = []
    soup = BeautifulSoup(open(fileName), "html.parser")
    rows = soup.find_all('tr')
    a = 0
    while a+3 <= len(rows) :
        while rows[a].td.text == '' :
            a = a + 1
            continue
        
        days = rows[a].find_all('td')
        lunchs = rows[a+1].find_all('td')
        dinners = rows[a+2].find_all('td')
        dinnerIndex = 1
        for i in range(1,6):
            resultItem = DayMenu()

            #날짜 구하기
            dayText = days[i].get_text()
            dayText = dayText[:dayText.find('(')].strip()
            resultItem.setDate(dayText)
            
            #점심메뉴 구하기
            for t in lunchs[i].find_all('p'):
                text = t.get_text().strip()
                if text != '':
                    resultItem.lunchMenus.append(text)
                    
            #저녁메뉴 구하기
            try:
                if int(lunchs[i]['rowspan'])> 1:
                    resultItem.isHoliday = True
                    resultItem.dinnerMenus = resultItem.lunchMenus
                    dinnerIndex = dinnerIndex - 1
            except:
                for t in dinners[dinnerIndex].find_all('p'):
                    text = t.get_text().strip()
                    if text != '':
                        resultItem.dinnerMenus.append(text)
            dinnerIndex = dinnerIndex + 1
            result.append(resultItem)
        a = a + 4
    return result


class FileInfo:
    def __init__(self):
        self.apkHash = ''
        self.apkFileName = FILE_APK
        self.menuListHash = ''
        self.menuListFileName = FILE_MENU+'.json'

    
def makeFileInfoFile() :
    outFile = open(os.path.join(RESOURCE_DIR, 'FileInfo.json'), 'w')
    info = FileInfo()
    info.menuListHash = hashlib.md5(open(os.path.join(RESOURCE_DIR, 'menuList', FILE_MENU + '.json'), 'rb').read()).hexdigest().upper()
    info.apkHash = hashlib.md5(open(os.path.join(RESOURCE_DIR, 'apk', FILE_APK), 'rb').read()).hexdigest().upper()
    outFile.write(json.dumps(info.__dict__))
    outFile.close()


def makeJsonMenuFile(openFile, outFile) :
    menuListDir = os.path.join(RESOURCE_DIR, 'menuList')
    dayItems = parseHtml(os.path.join(menuListDir, openFile))
    outFile = codecs.open(os.path.join(menuListDir, outFile), 'w', 'utf-8')
    outFile.write('[')
    count = len(dayItems)
    for i in dayItems:
        outFile.write(json.dumps(i.__dict__, ensure_ascii=False))
        count -= 1
        if count > 0:
            outFile.write(',')
    outFile.write(']')
    outFile.close()
    

makeJsonMenuFile(FILE_MENU+'.htm', FILE_MENU+'.json')
makeFileInfoFile()

