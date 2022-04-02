#====================================================={ all imports }==========================================================

from passlib.context import CryptContext

#===================================================={ global objects }========================================================

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

#================================={ compare plan password with hashed password function  }=====================================

def verify_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)

#========================================{ generate the hashed password function ==============================================

def get_password_hash(password):
    return pwd_context.hash(password)

#======================================================{ Code ends }===========================================================
