#====================================================={ all imports }==========================================================

from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

#===================================================={ global objects }========================================================

DATABASE_URL = 'sqlite:///dataleakage.db'

engine = create_engine(DATABASE_URL, connect_args={'check_same_thread':False})

SessionLocal = sessionmaker(bind=engine, autocommit=False, autoflush=False)

Base = declarative_base()

#======================================={ get db function return db connection object }=========================================

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

#======================================================{ Code ends }===========================================================
        
