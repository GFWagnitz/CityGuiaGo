# Base image
FROM python:3.13

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1
ENV DB_PATH=/data/db.sqlite3

# Setup phase
RUN apt-get update; \
apt-get install -y gunicorn; \
apt-get clean

# Create the data directory
RUN mkdir -p /data && chmod 777 /data

# Set work directory
WORKDIR /app
# Install poetry
RUN pip3 install poetry

# Copy poetry files
COPY pyproject.toml poetry.lock ./

# Install dependencies
RUN poetry config virtualenvs.create false \
    && poetry install --no-interaction --no-ansi --no-root

# Copy project
COPY . .

# Make sure the PYTHONPATH includes your project directory
ENV PYTHONPATH=/app

# Collect static files
RUN python3.12 manage.py collectstatic --noinput

# Run gunicorn
CMD gunicorn config.wsgi:application --bind 0.0.0.0:$PORT

HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:$PORT/ || exit 1