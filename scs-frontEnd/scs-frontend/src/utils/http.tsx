const env = "http://localhost:8099";

const headers = {
  Accept: "application/json",
  "Content-Type": "application/json",
};

interface FetchOptions {
  method: string;
  path: string;
  data?: any;
  token?: string;
}

const fetchRequest = async ({ method, path, data }: FetchOptions) => {
  try {
    const requestHeaders = { ...headers } as unknown as any;
    const token = sessionStorage.getItem("token");
    if (token) {
      requestHeaders["Authorization"] = `Bearer ${token}`;
    }

    const options: RequestInit = {
      method,
      headers: requestHeaders,
    };

    if (data) {
      options.body = JSON.stringify(data);
    }

    const response = await fetch(`${env}${path}`, options);
    return response;
  } catch (error) {
    console.error(error);
  }
};

export const preloginPost = async (path: string, data: any) => {
  return await fetchRequest({ method: "POST", path, data });
};

export const get = async (path: string) => {
  return await fetchRequest({ method: "GET", path });
};

export const post = async (path: string, data: any) => {
  return await fetchRequest({
    method: "POST",
    path,
    data,
  });
};
