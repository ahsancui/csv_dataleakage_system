#===================================================={ all imports }============================================================

from fastapi import FastAPI

from routes import agent, login, upload, download, guilty, register, files
import models, database

#==================================================={ global objects }==========================================================

app = FastAPI()        # FastAPI object

models.Base.metadata.create_all(bind = database.engine) # create database tables

#=================================================={ adding all routes }========================================================

app.include_router(login.router, prefix='/login', tags=['User'])

app.include_router(agent.router, prefix='/agent', tags=['Agent'])
app.include_router(register.router, prefix='/register', tags=['Agent'])

app.include_router(download.router, prefix='/download', tags=['File'])
app.include_router(upload.router, prefix='/upload', tags=['File'])
app.include_router(guilty.router, prefix='/guilty', tags=['File'])
app.include_router(files.router, prefix='/files', tags=['File'])

#======================================================{ Code ends }===========================================================
