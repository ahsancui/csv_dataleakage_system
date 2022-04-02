#===================================================={ all imports }============================================================

from fastapi import APIRouter, UploadFile, HTTPException, status, Depends
import pandas as pd
from sqlalchemy.orm import Session

from authentication import auth_distributer
from database import get_db
from models import OrigionalData, User

#==================================================={ global objects }==========================================================

router = APIRouter()   # API router object

#=================================================={ file upload route }========================================================

@router.post('/')
def upload(file:UploadFile, db:Session = Depends(get_db),  distributer:User = Depends(auth_distributer)): 
    name = file.filename # get file name
    
    # check if file is .csv file. if not, return error
    if not name.endswith('.csv'):
        raise HTTPException(status.HTTP_400_BAD_REQUEST, "File must be a CSV file")
    
    # reading the file using pandas and getting the number of rows and columns
    csv = pd.read_csv(file.file)   
    columns = csv.columns.__len__()
    rows = csv.__len__()
    
    # creating a new object of origional file information in database
    new_data = OrigionalData( name=name, columns=columns, rows=rows )
    
    # adding the new object to database
    db.add(new_data)
    db.commit()
    db.refresh(new_data)
    
    # generating a new file name using the id in database
    name = str(new_data.id) + '_' + name
    
    # saving the file in uploads/origional folder
    csv.to_csv(f'uploads/origional/{name}', index=False)
    
    # returning the success message
    return {"detail": "File uploaded successfully!"}

#======================================================{ Code ends }===========================================================
