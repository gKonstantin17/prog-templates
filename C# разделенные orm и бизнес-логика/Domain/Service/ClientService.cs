using Domain.DTO;
using Domain.Repositories;
using System.Text.RegularExpressions;

namespace Domain.Services
{
    public class ClientService
    {
        private readonly ClientRepository _clientRepository;

        public ClientService(string dalUrl, HttpClientHandler clientHandler)
        {
            _clientRepository = new ClientRepository(dalUrl, clientHandler);
        }
      
        public async Task<bool> CheckExistClient(ClientDto client)
        {
            var clients = await _clientRepository.Get();
            var existClient = clients.Where(c => c.Email == client.Email).SingleOrDefault();
            return existClient != null;
        }
    }
}
