const express = require('express');
const app = express();

app.get('/', (req, res) => {
  res.send(`
    <!DOCTYPE html>
    <html lang="pt-br">
    <head>
        <meta charset="UTF-8">
        <style>
            body {
                margin: 0;
                padding: 0;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                /* Fundo com degradê moderno (Azul escuro para o tema Condo) */
                background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
                color: white;
                text-align: center;
            }
            .card {
                background: rgba(255, 255, 255, 0.1);
                padding: 3rem;
                border-radius: 20px;
                backdrop-filter: blur(10px);
                box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
                border: 1px solid rgba(255, 255, 255, 0.18);
            }
            h1 { font-size: 3rem; margin-bottom: 10px; }
            p { font-size: 1.2rem; opacity: 0.9; }
            .version { 
                margin-top: 20px;
                font-weight: bold;
                background: #4caf50;
                padding: 5px 15px;
                border-radius: 50px;
                display: inline-block;
            }
        </style>
    </head>
    <body>
        <div class="card">
            <h1>🏢 SmartCondo</h1>
            <p>Sistema rodando com sucesso </p>
            <div class="version">Versão: v2.0 - CI/CD COMPLETO! 🏢</div>
        </div>
    </body>
    </html>
  `);
});

app.listen(8080, () => {
  console.log('Servidor rodando na porta 8080');
});