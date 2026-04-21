import os
import sys
import requests
import json

def generate_story_with_ai(title):
    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        print("Error: GEMINI_API_KEY no encontrada.")
        return None

    prompt = f"""
    Eres un experto en agilidad y desarrollo de software. 
    Expande el siguiente título de Historia de Usuario en una HU completa:
    Título: {title}
    
    La respuesta DEBE ser en formato Markdown y seguir esta estructura:
    # {title}
    
    ## Descripción
    [Una descripción clara del valor para el usuario]
    
    ## Requerimientos Funcionales
    - [Garantizar que...]
    - [Permitir que...]
    
    ## Criterios de Aceptación
    - [El sistema debe...]
    
    ## Definición de Hecho (DoD)
    - Código revisado.
    - Pruebas unitarias aprobadas.
    - Documentación actualizada.
    """

    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key={api_key}"
    headers = {'Content-Type': 'application/json'}
    data = {
        "contents": [{
            "parts": [{"text": prompt}]
        }]
    }

    try:
        response = requests.post(url, headers=headers, data=json.dumps(data))
        response.raise_for_status()
        result = response.json()
        return result['candidates'][0]['content']['parts'][0]['text']
    except Exception as e:
        print(f"Error llamando a la API: {e}")
        return f"# {title}\n\nError al generar el contenido automáticamente."

def create_github_issue(title, body):
    # Usamos el comando 'gh' de GitHub que ya está instalado en los runners
    try:
        # Escapamos comillas simples para evitar problemas con el shell
        safe_body = body.replace('"', '\\"').replace('`', '\\`')
        cmd = f'gh issue create --title "{title}" --body "{safe_body}" --label "historia-usuario"'
        os.system(cmd)
    except Exception as e:
        print(f"Error creando el issue: {e}")

def main():
    if not os.path.exists("docs/historias.txt"):
        print("Archivo docs/historias.txt no encontrado.")
        return

    with open("docs/historias.txt", "r", encoding="utf-8") as f:
        lines = f.readlines()

    for line in lines:
        title = line.strip()
        if not title or title.startswith("#"):
            continue
            
        print(f"Generando HU para: {title}...")
        body = generate_story_with_ai(title)
        if body:
            create_github_issue(title, body)
            print(f"Issue creada para: {title}")

if __name__ == "__main__":
    main()
