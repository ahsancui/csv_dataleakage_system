#====================================================={ all imports }==========================================================

from sqlalchemy import Column, String, Integer, Boolean, ForeignKey

from database import Base

#====================================================={ User model }===========================================================

class User(Base):
    __tablename__ = 'users'
    
    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    name = Column(String)
    email = Column(String, unique=True)
    password = Column(String)
    address = Column(String)
    contact = Column(String)
    isAgent = Column(Boolean)

#================================================{ Origional data model }======================================================

class OrigionalData(Base):
    __tablename__ = 'origional_data'
    
    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    name = Column(String)
    columns = Column(Integer)
    rows = Column(Integer)
            
#==================================================={ Fake data model }========================================================

class FakeData(Base):
    __tablename__ = 'fake_data'
    
    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    name = Column(String)
    columns = Column(Integer)
    rows = Column(Integer)
    agent_id = Column(Integer, ForeignKey('users.id'))
    origional_id = Column(Integer, ForeignKey('origional_data.id'))
    random = Column(Integer)

#======================================================{ Code ends }===========================================================
