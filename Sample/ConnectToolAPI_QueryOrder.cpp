#include <iostream>
#include <string>
#include <ctime>
#include <curl/curl.h>
#include <fstream>
#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <algorithm>

// RSA
#include <iostream>
#include <openssl/aes.h>
#include <openssl/evp.h>
#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <openssl/ssl.h>
#include <openssl/bio.h>
#include <openssl/err.h>
#include <assert.h>

/***
 * 編譯指令
 *  g++ ConnectToolAPI_QueryOrder.cpp -L/usr/lib -lssl -lcrypto -lcurl -o ConnectToolAPI_QueryOrder
 * ./ConnectToolAPI_QueryOrder
 */

std::string RSAstr = "";

void Base64Encode(const unsigned char *buffer,
                  size_t length,
                  char **base64Text)
{
  BIO *bio, *b64;
  BUF_MEM *bufferPtr;

  b64 = BIO_new(BIO_f_base64());
  bio = BIO_new(BIO_s_mem());
  bio = BIO_push(b64, bio);

  BIO_write(bio, buffer, length);
  BIO_flush(bio);
  BIO_get_mem_ptr(bio, &bufferPtr);
  BIO_set_close(bio, BIO_NOCLOSE);
  BIO_free_all(bio);

  *base64Text = (*bufferPtr).data;
}

RSA *createPrivateRSA(std::string key)
{
  RSA *rsa = NULL;
  const char *c_string = key.c_str();
  BIO *keybio = BIO_new_mem_buf((void *)c_string, -1);
  if (keybio == NULL)
  {
    return 0;
  }
  rsa = PEM_read_bio_RSAPrivateKey(keybio, &rsa, NULL, NULL);
  return rsa;
}

bool RSASign(RSA *rsa,
             const unsigned char *Msg,
             size_t MsgLen,
             unsigned char **EncMsg,
             size_t *MsgLenEnc)
{
  EVP_MD_CTX *m_RSASignCtx = EVP_MD_CTX_create();
  EVP_PKEY *priKey = EVP_PKEY_new();
  EVP_PKEY_assign_RSA(priKey, rsa);
  if (EVP_DigestSignInit(m_RSASignCtx, NULL, EVP_sha256(), NULL, priKey) <= 0)
  {
    return false;
  }
  if (EVP_DigestSignUpdate(m_RSASignCtx, Msg, MsgLen) <= 0)
  {
    return false;
  }
  if (EVP_DigestSignFinal(m_RSASignCtx, NULL, MsgLenEnc) <= 0)
  {
    return false;
  }
  *EncMsg = (unsigned char *)malloc(*MsgLenEnc);
  if (EVP_DigestSignFinal(m_RSASignCtx, *EncMsg, MsgLenEnc) <= 0)
  {
    return false;
  }
  EVP_MD_CTX_free(m_RSASignCtx);
  return true;
}
char *signMessage(std::string privateKey, std::string plainText)
{
  RSA *privateRSA = createPrivateRSA(privateKey);
  unsigned char *encMessage;
  char *base64Text;
  size_t encMessageLength;
  RSASign(privateRSA, (unsigned char *)plainText.c_str(), plainText.length(), &encMessage, &encMessageLength);
  Base64Encode(encMessage, encMessageLength, &base64Text);
  free(encMessage);
  return base64Text;
}

static size_t WriteCallback(void *contents, size_t size, size_t nmemb, void *userp)
{
  ((std::string *)userp)->append((char *)contents, size * nmemb);
  return size * nmemb;
}

static size_t header_callback(char *buffer, size_t size,
                              size_t nitems, void *userdata)
{
  std::string *headers = (std::string *)userdata;
  headers->append(buffer, nitems * size);
  return nitems * size;
}
char *appendCharToCharArray(char *array, char a)
{
  size_t len = strlen(array);

  char *ret = new char[len + 2];

  strcpy(ret, array);
  ret[len] = a;
  ret[len + 1] = '\0';

  return ret;
}

int main(void)
{
  CURL *curl;
  CURLcode res;
  std::string readBuffer;

  curl = curl_easy_init();
  if (curl)
  {
    // get X-Developer-Id
    std::string X_Developer_Id = "";
    std::string X_Developer_Id_header;
    X_Developer_Id_header.append("X-Developer-Id: ");
    X_Developer_Id_header.append(X_Developer_Id);

    // get time
    std::time_t t = std::time(0);
    std::tm *now = std::localtime(&t);
    char ts_buffer[256];
    strftime(ts_buffer, sizeof(ts_buffer), "%Y-%m-%dT%H:%M:%S.000Z", now);
    std::string timestamp{ts_buffer};

    // get transactionId
    std::string transactionId = "T2024013100000997";

    std::string plainData = "{\"requestNumber\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"timestamp\":\"";
    plainData.append(timestamp);
    plainData.append("\", \"transactionId\":\"");
    plainData.append(transactionId);
    plainData.append("\"}");

    std::cout << plainData << std::endl;
    const char *json = plainData.c_str();

    // get X_Signature
    char *X_Signature = signMessage(RSAstr, plainData);
    std::cout << X_Signature << std::endl;
    std::string X_Signature_header;
    X_Signature_header.append("X-Signature: ");
    X_Signature_header.append(X_Signature);
    X_Signature_header.erase(std::remove(X_Signature_header.begin(), X_Signature_header.end(), '\n'), X_Signature_header.cend());
    std::cout << X_Signature_header << std::endl;

    // get header
    struct curl_slist *chunk = NULL;
    chunk = curl_slist_append(chunk, "Accept:application/json; charset=utf-8");
    curl_slist_append(chunk, X_Developer_Id_header.c_str());
    curl_slist_append(chunk, "Content-Type: application/json; charset=utf-8");
    curl_slist_append(chunk, "Connection: keep-alive");
    curl_slist_append(chunk, X_Signature_header.c_str());

    curl_easy_setopt(curl, CURLOPT_URL, "https://r18gameapi.azurewebsites.net/api/CP/QueryOrder");
    curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "POST");
    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, chunk);
    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, json);
    curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, plainData.length());
    curl_easy_setopt(curl, CURLOPT_POST, 1);
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &readBuffer);

#if 0
    curl_easy_setopt(curl, CURLOPT_VERBOSE, 1L);
#endif

    res = curl_easy_perform(curl);

    if (res == CURLE_OK)
    {
      std::cout << "The answer from server is: " << std::endl;
      std::cout << readBuffer << std::endl;

      curl_global_cleanup();
      curl_easy_cleanup(curl);
      curl_slist_free_all(chunk);
    }
    else
    {
      std::cout << "curl_easy_perform() failed to get answer: " << curl_easy_strerror(res) << std::endl;

      curl_global_cleanup();
      curl_easy_cleanup(curl);
      curl_slist_free_all(chunk);
    }
  }
  return 0;
}
