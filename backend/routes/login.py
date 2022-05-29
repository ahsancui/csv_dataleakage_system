#===================================================={ all imports }============================================================

from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session

from authentication import generate_jwt_token
from database import get_db
import hashing
from models import User

#==================================================={ global objects }==========================================================

router = APIRouter()   # API router object

#====================================================={ Login route }===========================================================

@router.post('/')
def login(form_data: OAuth2PasswordRequestForm = Depends(), db:Session = Depends(get_db)):
    # check if user exists using email in database
    user:User = db.query(User).filter(User.email == form_data.username).first()
    
    # if user does not exist, return error
    if not user:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, "Email is not registered!")
    
    # if user exists, check if password is correct. If not, return error
    if not hashing.verify_password(form_data.password, user.password):
        raise HTTPException(status.HTTP_401_UNAUTHORIZED, "Invalid email or password!")
    
    # if password is correct, generate JWT token and return it
    token_data = {"id": user.id, "email": user.email, 'role': 'agent' if user.isAgent else 'distributer'}
    token = generate_jwt_token(token_data)
    
    # return JWT token
    return {"auth_token": token, "isAgent":user.isAgent}

#======================================================{ Code ends }===========================================================
