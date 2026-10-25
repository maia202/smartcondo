const { spawn } = require('node:child_process');
const path = require('node:path');

const isWindows = process.platform === 'win32';
const wrapper = path.join(__dirname, isWindows ? 'mvnw.cmd' : 'mvnw');
const command = isWindows ? process.env.ComSpec || 'cmd.exe' : wrapper;
const args = isWindows
  ? ['/d', '/s', '/c', `"${wrapper}" spring-boot:run`]
  : ['spring-boot:run'];

const application = spawn(command, args, {
  cwd: __dirname,
  stdio: 'inherit',
});

application.on('error', (error) => {
  console.error(`Não foi possível iniciar o SmartCondo: ${error.message}`);
  process.exitCode = 1;
});

application.on('exit', (code, signal) => {
  if (signal) {
    process.kill(process.pid, signal);
    return;
  }

  process.exitCode = code ?? 1;
});
