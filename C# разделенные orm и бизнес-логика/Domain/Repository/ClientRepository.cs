using Domain.DTO;
using Domain.Services;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using System.Net.Http;
using System.Text.Json;


namespace Domain.Repositories
{
    public class ClientRepository
    {
        private readonly string? _dalUrl;
        private readonly HttpClient _httpClient;
        public ClientRepository(string dalUrl, HttpClientHandler clientHandler)
        {
            _dalUrl = dalUrl;
            _httpClient = new HttpClient(clientHandler);
        }
        public async Task<List<ClientDto>?> Get()
        {
            var response = await _httpClient.GetAsync($"{_dalUrl}/clients");
            response.EnsureSuccessStatusCode();
            var content = await response.Content.ReadAsStringAsync();
            var clients = JsonConvert.DeserializeObject<List<ClientDto>>(content);
            return clients;
        }
        public async Task<ClientDto?> GetById(int id)
        {
           
                var response = await _httpClient.GetAsync($"{_dalUrl}/clients/{id}");
                response.EnsureSuccessStatusCode();
                var content = await response.Content.ReadAsStringAsync();
                var client = JsonConvert.DeserializeObject<ClientDto>(content);
                return client;
        }

        public async Task<int?> Post(ClientDto client)
        {
            var addedClient = await _httpClient.PostAsJsonAsync($"{_dalUrl}/clients", client);
            if (addedClient.IsSuccessStatusCode)
            {
                var createdClient = await addedClient.Content.ReadFromJsonAsync<int>();
                return createdClient;
            }
            return null;

        }
        public async Task<int?> Put(ClientDto client)
        {

            var response = await _httpClient.PutAsJsonAsync($"{_dalUrl}/products/{client.Id}", client);
            if (response.IsSuccessStatusCode)
            {
                var changedClient = await response.Content.ReadFromJsonAsync<int>();
                return changedClient;
            }
            return null;
        }
        public async Task<int?> Delete(int id)
        {
            var response = await _httpClient.DeleteAsync($"{_dalUrl}/orders/{id}");
            if (response.IsSuccessStatusCode)
            {
                var deletedClient = await response.Content.ReadFromJsonAsync<int>();
                return deletedClient;
            }
            return null;

        }
    }
}
