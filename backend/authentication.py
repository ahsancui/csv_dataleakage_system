#===================================================={ all imports }===========================================================


from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from jose import jwt, JWTError
from sqlalchemy.orm import Session

from database import get_db
from models import User

#=================================================={ all global objects }======================================================

SECRET_KEY = "JWT_SECRET_KEY"
ALGORITHM = "HS256"

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/login")


#==========================================={ generate json web token function }===============================================

def generate_jwt_token(payload):
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)

#============================================{ verify json web token function }================================================

def verify_jwt_token(token):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except JWTError:
        raise HTTPException(status.HTTP_401_UNAUTHORIZED, "Invalid token")

#============================================{ authenticate agent function }===================================================

def auth_agent(token = Depends(oauth2_scheme), db: Session = Depends(get_db)):
    payload = verify_jwt_token(token)
    agent = db.query(User).filter(User.id == payload["id"]).first()
    if not agent:
        raise HTTPException(status.HTTP_401_UNAUTHORIZED, "Invalid token")    
    return agent

#========================================={ authenticate distributer function }=================================================

def auth_distributer(token = Depends(oauth2_scheme), db: Session = Depends(get_db)):
    payload = verify_jwt_token(token)
    distributer = db.query(User).filter(User.id == payload["id"], User.isAgent == False).first()
    if not distributer:
        raise HTTPException(status.HTTP_401_UNAUTHORIZED, "Invalid token")    
    return distributer

#======================================================{ Code ends }===========================================================
