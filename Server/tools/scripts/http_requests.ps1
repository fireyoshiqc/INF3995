set-strictmode -version latest;


#{ Global variables

$credentials = @{"foo" = @{"username" = "foo"; "password" = "password1234"}; `
                 "bar" = @{"username" = "bar"; "password" = "password4321"}; `
                 "qux" = @{"username" = "qux"; "password" = "1234password"}}

$sessions = @{}

$server_addr = "127.0.0.1:80"

#}


#{ Functions

function get_ipv4_addr ( )
{
	$ip = (gwmi Win32_NetworkAdapterConfiguration | ? { $_.IPAddress -ne $null }).ipaddress
	if ( $ip[0] -match "." ) {
		return $ip[0]
	}
	else {
		return $ip[1]
	}
}


function send_post_users_login ( [string] $user, [string] $pass = "" )
{
	$uri = "http://" + $server_addr + "/users/login"
	$login_info = @{}
	if ( $($credentials.Keys).IndexOf($user) -ne -1 ) {
		$login_info = $credentials[$user]
	}
	else {
		Write-Warning "Credentials not found"
		$login_info = @{"username" = $user; "password" = $pass}
	}
	$login_info["device"] = "pc"
	$json_str = ConvertTo-Json $login_info
	
	$response = curl -Method POST -ContentType "application/json" -Uri $uri -Body $json_str -SessionVariable session -UseBasicParsing
	
	if ( $response.StatusCode -eq 200 ) {
		$sessions[$user] = $session
	}
	
	return $response
}


function send_post_users_logout ( [string] $user )
{
	$uri = "http://" + $server_addr + "/users/logout"
	$json_str = ConvertTo-Json @{"username" = $user}
	
	$response = $null
	if ( $sessions.Contains($user) ) {
		$response = curl -Method POST -ContentType "application/json" -Uri $uri -Body $json_str -WebSession $sessions[$user] -UseBasicParsing
	}
	else {
		$response = curl -Method POST -ContentType "application/json" -Uri $uri -Body $json_str -UseBasicParsing
	}
	
	return $response
}


function send_get_config_basic ( [string] $user )
{
	$uri = "http://" + $server_addr + "/config/basic"
	
	if ( $sessions.Contains($user) ) {
		curl -Method GET -Uri $uri -WebSession $sessions[$user] -UseBasicParsing
	}
	else {
		curl -Method GET -Uri $uri -UseBasicParsing
	}
}


function send_get ( [string] $url, [string] $user )
{
	$uri = "http://" + $server_addr + $url
	
	if ( $sessions.Contains($user) ) {
		curl -Method GET -Uri $uri -WebSession $sessions[$user] -UseBasicParsing
	}
	else {
		curl -Method GET -Uri $uri -UseBasicParsing
	}
}

#}


#{ Auto-exec

$server_addr = (get_ipv4_addr) + ":80"

#}

