<?php
require('vendor/autoload.php');

error_reporting(E_ALL);

/* Allow the script to hang around waiting for connections. */
set_time_limit(0);

/* Turn on implicit output flushing so we see what we're getting
 * as it comes in. */
ob_implicit_flush();


$address = '127.0.0.1';
$port = 10000;

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, "http://192.168.28.117:4567/test/subscribe/player_login/" . urlencode("http://$address:$port/"));
curl_setopt($ch, CURLOPT_HEADER, false);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

echo "Sending subscription request\n";
$response = curl_exec($ch);

curl_close($ch);

echo "Got $response\n\n";

use Psr\Http\Message\ServerRequestInterface;
use React\EventLoop\Factory;
use React\Http\Response;
use React\Http\Server;

$loop = React\EventLoop\Factory::create();

$server = new Server(function (ServerRequestInterface $request) {
    echo "Request {$request->getUri()->getPath()}\n";

    return new Response(
        200,
        array(
            'Content-Type' => 'text/plain'
        ),
        "Hello World!\n"
    );
});

$socket = new \React\Socket\Server("$address:$port", $loop);
$server->listen($socket);
echo 'Listening on ' . str_replace('tcp:', 'http:', $socket->getAddress()) . PHP_EOL;

$loop->run();
?>