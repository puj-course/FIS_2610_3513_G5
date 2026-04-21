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
    Eres un experto en agilidad y desarrollo de software (Product Owner). 
    Expande el siguiente título de Historia de Usuario en una HU completa y profesional:
    Título: {title}
    
    La respuesta DEBE estar en español, ser en formato Markdown y seguir ESTRICTAMENTE esta estructura:
    
    ## Descripción
    [Redacta una descripción detallada siguiendo el formato: "Como [rol], quiero [acción] para [beneficio]". Explica el contexto y el valor de negocio.]
    
    ## Criterios de Aceptación
    - [Escenario 1: Dado que... cuando... entonces...]
    - [Escenario 2: ...]
    
    ## Requerimientos Técnicos
    - [Detalle técnico 1]
    
    ## Definición de Hecho (DoD)
    - Código sigue estándares de calidad.
    - Pruebas unitarias al 80% de cobertura.
    - Revisión de pares (Code Review) completada.
    - Funcionalidad verificada en ambiente de desarrollo.
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
        return f"## Descripción\nError al generar el contenido para: {title}"

def create_github_issue(title, body):
    # Usamos un archivo temporal para el cuerpo para evitar problemas de escape en el shell
    temp_file = "temp_hu_body.md"
    try:
        with open(temp_file, "w", encoding="utf-8") as f:
            f.write(body)
        
        # El título debe empezar con HU - como pidió el usuario
        hu_title = f"HU - {title}"
        
        # Usamos --body-file que es más robusto para Markdown largo
        cmd = f'gh issue create --title "{hu_title}" --body-file "{temp_file}" --label "historia-usuario"'
        os.system(cmd)
        
        if os.path.exists(temp_file):
            os.remove(temp_file)
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
        # Ignorar líneas vacías o comentarios
        if not title or title.startswith("#"):
            continue
            
        print(f"Generando HU para: {title}...")
        body = generate_story_with_ai(title)
        if body:
            create_github_issue(title, body)
            print(f"Issue creada: HU - {title}")

if __name__ == "__main__":
    main()
