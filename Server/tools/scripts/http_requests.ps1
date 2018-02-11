set-strictmode -version latest;


#{ Global variables

$credentials = @{"foo" = @{"username" = "foo"; "password" = "password1234"}; `
                 "bar" = @{"username" = "bar"; "password" = "password4321"}; `
                 "qux" = @{"username" = "qux"; "password" = "1234password"}}

$sessions = @{}

$server_addr = "132.207.232.168:80"

#}


#{ Functions

function send_post_users_login ( [string] $user )
{
	$uri = "http://" + $server_addr + "/users/login"
	$json_str = ConvertTo-Json $credentials[$user]
	
	$response = curl -Method POST -ContentType "application/json" -Uri $uri -Body $json_str -SessionVariable session
	
	if ( $response.StatusCode -eq 200 ) {
		$sessions[$user] = $session
	}
	
	return $response
}


function send_post_users_logout ( [string] $user )
{
	$uri = "http://" + $server_addr + "/users/logout"
	$json_str = ConvertTo-Json @{"username"=$credentials[$user]["username"]}
	
	$response = $null
	if ( $sessions.Contains($user) ) {
		$response = curl -Method POST -ContentType "application/json" -Uri $uri -Body $json_str -WebSession $sessions[$user]
	}
	else {
		$response = curl -Method POST -ContentType "application/json" -Uri $uri -Body $json_str
	}
	
	return $response
}


function send_get_config_basic ( [string] $user )
{
	$uri = "http://" + $server_addr + "/config/basic"
	
	if ( $sessions.Contains($user) ) {
		curl -Method GET -Uri $uri -WebSession $sessions[$user]
	}
	else {
		curl -Method GET -Uri $uri
	}
}


function send_get ( [string] $user, [string] $url )
{
	$uri = "http://" + $server_addr + $url
	
	if ( $sessions.Contains($user) ) {
		curl -Method GET -Uri $uri -WebSession $sessions[$user]
	}
	else {
		curl -Method GET -Uri $uri
	}
}

#}

