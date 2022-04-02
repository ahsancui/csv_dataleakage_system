#===================================================={ all imports }============================================================

from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel, EmailStr
from sqlalchemy.orm import Session 
from typing import List

from authentication import auth_agent, auth_distributer
from database import get_db
from models import User

#==================================================={ global objects }==========================================================

router = APIRouter()   # API router object

#==================================================={ Response Schema }=========================================================

class ResponseSchema(BaseModel):
    id:int
    name:str
    email:EmailStr
    address:str
    contact:str
    class Config:
        orm_mode = True

#================================================{ Single agent route }========================================================

@router.get('/{id}', response_model=ResponseSchema)
def single_agent(id:int, db:Session = Depends(get_db), distributer:User = Depends(auth_distributer)):
    agent = db.query(User).filter(User.id == id, User.isAgent == True).first()
    if not agent:
        raise HTTPException(status.HTTP_404_NOT_FOUND, f"Agent with id {id} is not available!")
    return agent

#================================================={ all agents route }=========================================================

@router.get('/all', response_model=List[ResponseSchema])
def all_agents(db:Session = Depends(get_db), distributer:User = Depends(auth_distributer)):
    return db.query(User).filter(User.isAgent == True).all()


#================================================{ delete agent route }========================================================
@router.delete('/{id}')
def delete_agent(id:int, db:Session = Depends(get_db), distributer:User = Depends(auth_distributer)):
    agent = db.query(User).filter(User.id == id, User.isAgent==True).first()
    if not agent:
        raise HTTPException(status.HTTP_404_NOT_FOUND, f"Agent with id {id} is not available!")
    
    db.delete(agent)
    db.commit()
    return {"detail":f"Agent with id {agent.id} is successfully deleted!"}

#======================================================{ Code ends }===========================================================
