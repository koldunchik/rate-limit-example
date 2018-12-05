function get_response {
    response=$(curl --write-out "%{http_code}" --silent -X GET $url)
}

function test {
    http_expected_code=$1
    if (( $response != $http_expected_code )); then
        printf "TEST failed $http_expected_code vs $response\n"
        exit 1
    fi
}

function check_http_code {
   code=$1
   n=$2

   printf "Check HTTP code $code\n"
   for (( i=1; i<=$n; i++ ))
   do
      printf "$i;"
      get_response
      test $code
   done

   printf "\n"
}