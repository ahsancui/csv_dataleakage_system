#===================================================={ all imports }============================================================

from fastapi import APIRouter, UploadFile, HTTPException, status, Depends
import pandas as pd
from sqlalchemy.orm import Session

import algorithm
from authentication import auth_distributer
from database import get_db
from models import OrigionalData, User, FakeData

#==================================================={ global objects }==========================================================

router = APIRouter()   # API router object

#===================================================={ guilty route }===========================================================

@router.post('/{id}')
def guilty(id:int , clone_file:UploadFile, db:Session = Depends(get_db), distributer:User = Depends(auth_distributer)):
    # checking if the file is csv or not
    clone_name = clone_file.filename
    if not clone_name.endswith('.csv'):
        raise HTTPException(status.HTTP_400_BAD_REQUEST, "File must be a CSV file")
    
     # checking if file exist or not in database
    origional = db.query(OrigionalData).filter(OrigionalData.id == id).first()
    
    # if file not found returning error
    if not origional:
        raise HTTPException(status.HTTP_404_NOT_FOUND, f"File with id {id} is not available!")
    
    # reading the clone file
    clone_file = pd.read_csv(clone_file.file)
    
    # finding all the fake data of the origional file
    fake_files = db.query(FakeData).filter(FakeData.origional_id == origional.id).all()
    
    # if no fake data is found for respective file then agents are in the safe zone
    if not fake_files:
        raise HTTPException(status.HTTP_404_NOT_FOUND, f"Our agents are innocents!")
    
    # creating empty list to store response
    guilty_agents = list()
    
    # iterating over all the fake files and finding the guilty agents
    for fake in fake_files:
        # empty dictionary to store the data of indiviual agent
        guilty_agent = dict()
        
        # loading fake file of the respective agent 
        fake_file = pd.read_csv(f'uploads/fake/{str(fake.id)}_{str(fake.name)}')
        
        # finding the guilt percentage of the agent
        percent  = algorithm.findGuilty(clone_file, fake_file, fake.random)
        
        # finding the detail of agent from the database
        agent = db.query(User).filter(User.id == fake.agent_id).first()
        
        # preparing the agent response data
        guilty_agent['percent'] = percent
        guilty_agent['id'] = agent.id
        guilty_agent['name'] = agent.name
        guilty_agent['email'] = agent.email
        
        # append the indiviual agent data to the list
        guilty_agents.append(guilty_agent)
    
    # sending the list guilty agents
    return guilty_agents

#======================================================{ Code ends }===========================================================
