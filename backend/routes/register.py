#===================================================={ all imports }============================================================

from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel, EmailStr
from sqlalchemy.orm import Session 

from authentication import auth_distributer
from database import get_db
import hashing
from models import User

#==================================================={ global objects }==========================================================

router = APIRouter()   # API router object

#==================================================={ request schema }==========================================================

class RegisterSchema(BaseModel):
    name:str
    email:EmailStr
    password:str
    address:str
    contact:str
    isAgent:bool = True

#==================================================={ register route }==========================================================

@router.post('/')
def register_agent(body:RegisterSchema, db:Session = Depends(get_db), distributer:User =Depends(auth_distributer)):
    # check if agent is already register or not
    already_register = db.query(User).filter(User.email == body.email).first()
    
    # if already register then return error
    if already_register:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, f"Agent with email already exists!")
    
    # hasing the password
    hashed_password = hashing.get_password_hash(body.password)
    
    # create new agent object
    new_agent = User( 
        name=body.name, 
        email=body.email,
        password=hashed_password,
        address = body.address, 
        contact =body.contact,
        isAgent = body.isAgent
        )
    
    # adding new agent to database
    db.add(new_agent)
    db.commit()
    
    # returning success message
    return {"detail": f"User registered successfully"}

#======================================================{ Code ends }===========================================================
