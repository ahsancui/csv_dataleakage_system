#===================================================={ all imports }============================================================

from fastapi import APIRouter, HTTPException, status, Depends
from fastapi.responses import StreamingResponse
import io
import pandas as pd
from sqlalchemy.orm import Session

import algorithm
from authentication import auth_agent
from database import get_db
from models import OrigionalData, FakeData, User

#==================================================={ global objects }==========================================================

router = APIRouter()   # API router object

#====================================================={ Login route }===========================================================
@router.get('/{id}')
def download(id:int, db:Session = Depends(get_db), agent:User = Depends(auth_agent)):
    # checking if file exist or not in database
    origional = db.query(OrigionalData).filter(OrigionalData.id == id).first()
    # if file not found returning error
    if not origional:
        raise HTTPException(status.HTTP_404_NOT_FOUND, f"File with id {id} is not available!")
    
    # if file exists then loading the file from directory
    origionalFile = pd.read_csv(f'uploads/origional/{str(origional.id)}_{str(origional.name)}')
    
    # user is distributer then sending the origional file
    if agent.isAgent == False:
        stream = io.StringIO()
        origionalFile.to_csv(stream, index=False)
    
        response = StreamingResponse(iter([stream.getvalue()]), media_type="text/csv")
        response.headers["Content-Disposition"] = "attachment; filename=export.csv"
        return response

    # checking if the agent already download the file
    fake = db.query(FakeData).filter(FakeData.origional_id == origional.id, FakeData.agent_id == agent.id ).first()
    
    # if agent not download the file the generate new fake data
    if not fake:
        fakeFile, random = algorithm.generateFake(origionalFile)
        
        fake = FakeData(name = origional.name, 
                        columns = fakeFile.columns.__len__(), 
                        rows = fakeFile.__len__(), 
                        agent_id = agent.id, 
                        origional_id = origional.id,
                        random = random)
        db.add(fake)
        db.commit()
        db.refresh(fake)
        fakeFile.to_csv(f'uploads/fake/{str(fake.id)}_{str(fake.name)}', index=False)
    
    # loading fake file 
    fakeFile = pd.read_csv(f'uploads/fake/{str(fake.id)}_{str(fake.name)}')
    
    responseFile = algorithm.prepareData(origionalFile, fakeFile, fake.random)
    
    stream = io.StringIO()
    responseFile.to_csv(stream, index=False)
    
    response = StreamingResponse(iter([stream.getvalue()]), media_type="text/csv")
    response.headers["Content-Disposition"] = "attachment; filename=export.csv"

    return response

#======================================================{ Code ends }===========================================================
