import os
import sys
import requests
import json
import subprocess

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

    url = f"https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash-lite:generateContent?key={api_key}"
    headers = {'Content-Type': 'application/json'}
    data = {
        "contents": [{
            "parts": [{"text": prompt}]
        }]
    }

    try:
        response = requests.post(url, headers=headers, data=json.dumps(data))
        if response.status_code != 200:
            print(f"Error de API (Status {response.status_code}): {response.text}")
            return f"## Descripción\nError de API (Status {response.status_code}).\n\n**Detalle:**\n```json\n{response.text}\n```"
        
        result = response.json()
        return result['candidates'][0]['content']['parts'][0]['text']
    except Exception as e:
        print(f"Error llamando a la API: {e}")
        return f"## Descripción\nError excepcional al generar el contenido.\n\n**Error:** {str(e)}"

def create_github_issue(title, body):
    # El título debe empezar con HU - como pidió el usuario
    hu_title = f"HU - {title}"
    print(f"Preparando para crear issue: {hu_title}")
    
    # Asegurarnos de que el cuerpo incluya el título original y esté completo
    full_body = f"# {title}\n\n{body}"
    
    # Usamos un archivo temporal para el cuerpo para evitar problemas de escape en el shell
    temp_file = "temp_hu_body.md"
    try:
        with open(temp_file, "w", encoding="utf-8") as f:
            f.write(full_body)
        
        # Primero intentamos asegurar que la etiqueta existe
        print("Verificando/Creando etiqueta 'historia-usuario'...")
        subprocess.run(["gh", "label", "create", "historia-usuario", "--color", "f29513"], capture_output=True)

        # Usamos subprocess.run con una lista para evitar problemas con comillas y caracteres especiales
        cmd = [
            "gh", "issue", "create",
            "--title", hu_title,
            "--body-file", temp_file,
            "--label", "historia-usuario"
        ]
        
        print(f"Ejecutando: {' '.join(cmd)}")
        result = subprocess.run(cmd, capture_output=True, text=True)
        if result.returncode == 0:
            issue_url = result.stdout.strip()
            print(f"✅ Éxito: Issue creada en {issue_url}")
            
            # Intentar añadir el issue al proyecto 63 automáticamente
            project_number = 63
            org = os.getenv("ORGANIZATION", "puj-course")
            print(f"Añadiendo a proyecto #{project_number} en {org}...")
            
            add_cmd = ["gh", "project", "item-add", str(project_number), "--owner", org, "--url", issue_url]
            add_result = subprocess.run(add_cmd, capture_output=True, text=True)
            
            if add_result.returncode == 0:
                print(f"🚀 Issue vinculada al proyecto #{project_number}")
            else:
                print(f"⚠️ No se pudo vincular al proyecto: {add_result.stderr}")
        else:
            print(f"❌ Error ejecutando gh issue create: {result.stderr}")
            # Intento sin la etiqueta por si acaso falló por eso
            print("Reintentando sin etiqueta...")
            cmd_no_label = ["gh", "issue", "create", "--title", hu_title, "--body-file", temp_file]
            result_retry = subprocess.run(cmd_no_label, capture_output=True, text=True)
            if result_retry.returncode == 0:
                print(f"✅ Éxito (sin etiqueta): {result_retry.stdout.strip()}")
            else:
                print(f"❌ Error definitivo: {result_retry.stderr}")
        
        if os.path.exists(temp_file):
            os.remove(temp_file)
    except Exception as e:
        print(f"💥 Error excepcional creando el issue: {e}")

def main():
    if not os.path.exists("docs/historias.txt"):
        print("❌ Archivo docs/historias.txt no encontrado.")
        return

    # Intentar leer con diferentes codificaciones por si acaso
    content = ""
    for enc in ['utf-8', 'latin-1', 'utf-16']:
        try:
            with open("docs/historias.txt", "r", encoding=enc) as f:
                content = f.read()
            print(f"Lectura exitosa con codificación: {enc}")
            break
        except Exception:
            continue
    
    if not content:
        print("❌ No se pudo leer el archivo docs/historias.txt con ninguna codificación.")
        return

    lines = content.splitlines()
    print(f"Se encontraron {len(lines)} líneas en historias.txt")

    for line in lines:
        raw_line = line.strip()
        # Ignorar líneas vacías
        if not raw_line:
            continue
            
        # Si la línea empieza con #, quitarle los # y espacios iniciales
        title = raw_line.lstrip("#").strip()
            
        print(f"\n--- Procesando: {title} ---")
        body = generate_story_with_ai(title)
        if body:
            create_github_issue(title, body)
        else:
            print("⚠️ No se generó contenido para esta historia.")

if __name__ == "__main__":
    print("🚀 Iniciando generador de Historias de Usuario...")
    main()
