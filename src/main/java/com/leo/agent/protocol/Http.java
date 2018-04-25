package com.leo.agent.protocol;

public interface Http {
    String VERSION_1_0 = "HTTP/1.0";
    String VERSION_1_1 = "HTTP/1.1";

    String METHOD_OPTIONS = "OPTIONS";
    String METHOD_HEAD = "HEAD";
    String METHOD_GET = "GET";
    String METHOD_POST = "POST";
    String METHOD_PUT = "PUT";
    String METHOD_DELETE = "DELETE";
    String METHOD_TRACE = "TRACE";
    String METHOD_CONNECT = "GET";

    String RESPONSE_100 = "100 Continue";
    String RESPONSE_101 = "101 Switching Protocols";
    String RESPONSE_102 = "102 Processing";
    String RESPONSE_200 = "200 OK";
    String RESPONSE_201 = "201 Created";
    String RESPONSE_202 = "202 Accepted";
    String RESPONSE_203 = "203 Non-Authoriative Information";
    String RESPONSE_204 = "204 No Content";
    String RESPONSE_205 = "205 Reset Content";
    String RESPONSE_206 = "206 Partial Content";
    String RESPONSE_207 = "207 Multi-Status";
    String RESPONSE_300 = "300 Multiple Choices";
    String RESPONSE_301 = "301 Moved Permanently";
    String RESPONSE_302 = "302 Found";
    String RESPONSE_303 = "303 See Other";
    String RESPONSE_304 = "304 Not Modified";
    String RESPONSE_305 = "305 User Proxy";
    String RESPONSE_306 = "306 Unused";
    String RESPONSE_307 = "307 Temporary Redirect";
    String RESPONSE_400 = "400 Bad Request";
    String RESPONSE_401 = "401 Unauthorized";
    String RESPONSE_402 = "402 Payment Granted";
    String RESPONSE_403 = "403 Forbidden";
    String RESPONSE_404 = "404 File Not Found";
    String RESPONSE_405 = "405 Method Not Allowed";
    String RESPONSE_406 = "406 Not Acceptable";
    String RESPONSE_407 = "407 Proxy Authentication Required";
    String RESPONSE_408 = "408 Request Time-out";
    String RESPONSE_409 = "409 Conflict";
    String RESPONSE_410 = "410 Gone";
    String RESPONSE_411 = "411 Length Required";
    String RESPONSE_412 = "412 Precondition Failed";
    String RESPONSE_413 = "413 Request Entity Too Large";
    String RESPONSE_414 = "414 Request-URI Too Large";
    String RESPONSE_415 = "415 Unsupported Media Type";
    String RESPONSE_416 = "416 Requested range not satisfiable";
    String RESPONSE_417 = "417 Expectation Failed";
    String RESPONSE_422 = "422 Unprocessable Entity";
    String RESPONSE_423 = "423 Locked";
    String RESPONSE_424 = "424 Failed Dependency";
    String RESPONSE_500 = "500 Internal Server Error";
    String RESPONSE_501 = "501 Not Implemented";
    String RESPONSE_502 = "502 Bad Gateway";
    String RESPONSE_503 = "503 Service Unavailable";
    String RESPONSE_504 = "504 Gateway Timeout";
    String RESPONSE_505 = "505 HTTP Version Not Supported";
    String RESPONSE_507 = "507 Insufficient Storage";

    String CRLF = "\r\n";
}
