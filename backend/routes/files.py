#===================================================={ all imports }============================================================

from fastapi import APIRouter, Depends
from pydantic import BaseModel
from sqlalchemy.orm import Session
from typing import List

from authentication import auth_agent
from database import get_db
from models import OrigionalData, User

#==================================================={ global objects }==========================================================

router = APIRouter()   # API router object

#====================================================={ Login route }===========================================================

class ResponseSchema(BaseModel):
    id:int
    name:str
    class Config:
        orm_mode = True

#====================================================={ Login route }===========================================================

@router.get('/', response_model=List[ResponseSchema])
def files( db:Session = Depends(get_db), agent:User = Depends(auth_agent)):
    files = db.query(OrigionalData).all()    
    return files

#======================================================{ Code ends }===========================================================
