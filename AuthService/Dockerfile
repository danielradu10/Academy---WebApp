FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .

# dependentele de sistem necesare
RUN apt-get update && apt-get install -y \
    libmariadb-dev gcc \
    && apt-get clean

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 50051 8000

CMD ["sh", "-c", "python AuthenticationService.py & uvicorn main:app --host 0.0.0.0 --port 8000"]
