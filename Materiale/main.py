from fastapi import FastAPI
from controllers import MaterialsController
from middleware.ValidatorMiddleware import ValidatorMiddleware
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()
app.add_middleware(ValidatorMiddleware)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
app.include_router(MaterialsController.router)

