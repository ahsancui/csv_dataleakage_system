#===================================================={ all imports }===========================================================

import pandas as pd
from random import randint

#============================================={ generate fake data function ===================================================

def generateFake(data:pd.DataFrame, percent:int=10):
    length = data.__len__()
    columns = data.columns
    
    fakeData = pd.DataFrame(columns=columns)
    fakeRowsNum = int(length * percent / 100)
    
    for row in range(fakeRowsNum):
        prev = -1
        fakeRow = dict()
        for column in columns:
            curr = randint(0, length-1)
            while(prev == curr):
                curr = randint(0, length-1)
            prev = curr
            
            fakeRow[column] = data[column].loc[curr]
            curr = -1
                
        fakeData = fakeData.append(fakeRow, ignore_index=True)
    
    randomNum = randint(1, int((length-1)/fakeRowsNum))
    
    return fakeData, randomNum

#======================================={ add fake data inside origional file }================================================

def prepareData(origional:pd.DataFrame, fake:pd.DataFrame, random):
         
    for i in range(1, fake.__len__()+1):    
        origional.loc[i*random] = fake.loc[i-1]    
    return origional

#=============================================={ find the guilty agent }=======================================================

def findGuilty(clone:pd.DataFrame, fake:pd.DataFrame, random:int):
    percent = 0
    length = fake.__len__()
    
    for i in range(1, length+1):
        print()
        if all(clone.loc[i*random].values == fake.loc[i-1].values):
            percent+=1
    percent = percent*100/length
    return percent 
    
#======================================================{ Code ends }===========================================================
